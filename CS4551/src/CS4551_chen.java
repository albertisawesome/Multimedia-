import java.util.Scanner;

/*******************************************************
 * CS4551 Multimedia Software Systems @ Author: Elaine Kang
 * 
 * 
 * Template Code - demonstrate how to use MImage class
 *******************************************************/

public class CS4551_chen {
	public static void main(String[] args) {
		// the program expects one command line argument
		// if there is no command line argument, exit the program
		if (args.length != 1) {
			usage();
			System.exit(1);
		}

		System.out.println("--Welcome to Multimedia Software System--");

		// Create an Image object with the input PPM file name.
		MImage img = new MImage(args[0]);
		System.out.println(img);

		
		boolean done = false;
		String imgName = args[0];
		Scanner in = new Scanner(System.in);
		
		while(!done) {
			int choice = menu(in);
			switch (choice) {
			case 1:
				taskOne(imgName);
				break;
			case 2:
				taskTwo(imgName.replace(".ppm", "-gray.ppm"));
				break;
			case 3:
				printLUT(taskThree());
				index(imgName);
				break;
			case 4:
				done = true;
			}
		}
		
	}

	public static void usage() {
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
	
	public static int menu(Scanner in) {
		int choice = 0;
		System.out.println();
		System.out.println("Main Menu-----------------------------------");
		System.out.println("1. Conversion to Gray-scale Image (24bits->8bits)");
		System.out.println("2. Conversion to Binary Image using Ordered Dithering (k=4)");
		System.out.println("3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization (24bits->8bits)");
		System.out.println("4. Quit");
		System.out.println("Please enter the task number [1-4]:");
		
		try {
			choice = in.nextInt();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return choice;
				
	}
		
	public static void taskOne(String img) {
		MImage i = new MImage(img);
		int[] rgb = new int[3];
		
		for(int y = 0; y < i.getH(); y++) {
			for(int x = 0; x < i.getW(); x++) {
				i.getPixel(x, y, rgb);
				
				int red = rgb[0];
				int green = rgb[1];
				int blue = rgb[2];
								
				int gray = (int) Math.round(0.299 * red + 0.587 * green + 0.114 * blue);
				
				if(gray >= 0 && gray <= 255) {
					int[] tempRGB = {gray, gray, gray};
					i.setPixel(x, y, tempRGB);
				}
				else {
					int truncated = (int) Math.floor(gray);
					int[] truncate = {truncated, truncated, truncated};
					i.setPixel(x, y, truncate);
				}
						
			}
		}

		
		i.write2PPM(i.getName().replace(".ppm", "-gray.ppm"));
	
	}
	
	public static void taskTwo(String image) {
		MImage img = new MImage(image);
		int[][] matrix = {{0, 8, 2, 10}, 
						  {12, 4, 14, 6}, 
						  {3, 11, 1, 9}, 
						  {15, 7, 13, 5}};
		
		
		int k = 4;	
		int[] rgb = new int[3];
		
		for(int y = 0; y < img.getH(); y++) {
			for(int x = 0; x < img.getW(); x++) {
				img.getPixel(x, y, rgb);
				int i = y % k;
				int j = x % k;
						
				int[] black = {0, 0, 0};
				int[] white = {255, 255, 255};
				
				int intensity = (int) (rgb[0] * ((k * k) + 1) / (256));
				
				if(intensity > matrix[i][j]) {
					img.setPixel(x, y, white);
				}
				else {
					img.setPixel(x, y, black);
				}
			}
		}
					
		img.write2PPM(image.replace("-gray.ppm", "-OD4.ppm"));
	
	}
	
	
	public static int[][] taskThree() {
		int[][] table = new int[256][3];

		for(int i = 0; i < 256; i++) {
			//for(int j = 0; j < 1; j++) {
				int red = i >> 5;
				int green = (i >> 2) & 7;
				int blue = i & 3;
				//int col = j;
				table[i][0] = red * 32 + 16;
				table[i][1] = green * 32 + 16;
				table[i][2] = blue * 64 + 32;
			//}
		}		
		
		return table;
		
	}
	
	public static void printLUT(int[][] LUT) {
		System.out.println("LUT by UCQ");
		System.out.println("Index R G B");
		System.out.println("____________________________");
		
		for(int i = 0; i < LUT.length; i++) {
			System.out.print(i + " ");
			for(int j = 0; j < LUT[i].length; j++) {
				System.out.print(LUT[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public static void index(String name) {
		MImage img = new MImage(name);
		int[] rgb = new int[3];
		
		MImage output = new MImage(img.getW(), img.getH());
		for(int y = 0; y < img.getH(); y++) {
			for(int x = 0; x < img.getW(); x++) {
				img.getPixel(x, y, rgb);
				int red = rgb[0] >> 5;
				int green = rgb[1] >> 5;
				int blue = rgb[2] >> 6;
				int bitValue = (red << 5) | (green << 2) | blue;
				rgb[0] = rgb[1] = rgb[2] = bitValue;
				output.setPixel(x, y, rgb);
				
			}
		}
		
		output.write2PPM(name.replace(".ppm", "") + "-index.ppm");
		
		MImage outputTwo = new MImage(output.getW(), output.getH());
		
		int[] rgb2 = new int[3];
		for(int y = 0; y < img.getH(); y++) {
			for(int x = 0; x < img.getW(); x++) {
				output.getPixel(x, y, rgb2);
				int index = rgb2[0];
				int[] newRGB = getColor(index, taskThree());
				outputTwo.setPixel(x, y, newRGB);
			}
		}
		
		outputTwo.write2PPM(name.replace(".ppm", "") + "-QT8.ppm");
		
	}
	
	public static int[] getColor(int index, int[][] LUT) {
		int[] color = new int[3];
		
		for(int i = 0; i < LUT.length; i++) {
				if(index == i) {
					color[0] = LUT[i][0];
					color[1] = LUT[i][1];
					color[2] = LUT[i][2];
				}
		}
		
		return color;
	}
}
