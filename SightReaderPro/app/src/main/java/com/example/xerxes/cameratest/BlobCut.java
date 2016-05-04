// Blob-cutting utility
// Takes BMP image input
// Returns BMP image of input with notes separated into blobs
package com.example.xerxes.cameratest;

import android.graphics.Bitmap;


class BlobCut {
        public BlobCut(){}
		
        private void get_RGB(int pixel, int[] RGB) {
				// Takes ARGB_8888 pixel values, puts separate RGB values into RGB[]
                RGB[0] = (pixel & 0x00ff0000) >> 16;
                RGB[1] = (pixel & 0x0000ff00) >> 8;
                RGB[2] = (pixel & 0x000000ff);
        }

        private int get_grey(int pix) {
				// Takes ARGB_8888 pixel value, returns 0 <= value <= 255
                int[] RGB = new int[3];
                get_RGB(pix, RGB);
                return (RGB[0] + RGB[1] + RGB[2]) / 3;
        }

        private int put_grey(int val) {
				// Takes int greyscale 0 <= value <= 255, returns ARGB_8888 pixel value
                int t_val = val;
                if (val < 0) {t_val = 0;}
                else if (val > 255) {t_val = 255;}
                int r = t_val << 16;
                int g = t_val << 8;
                int b = t_val;
                return 0xff000000 | r | g | b;
        }
		
		
		private void column_filter_image(int[] image, int im_height, int im_width, int[] filter, int f_size, int[] out) {
				int f_sum = 0;
                int p_sum;
                int p_val;
                int edge = f_size/2;
				
				for (int i = 0; i<f_size; i++) {
						f_sum += filter[i];
				}
				
				for (int row = 0; row < im_height; row++) {
						for (int col = 0; col < im_width; col++) {
								p_sum = 0;
								for (int i = 0; i<f_size; i++) {
										if (row-edge+i < 0 || row-edge+i >= im_height) {
												p_val = 0xffffffff;
										} else {
												p_val = image[(row-edge+i)*im_width + col];
										}
										p_sum += get_grey(p_val) * filter[i];
								}
								out[row*im_width + col] = put_grey(p_sum/f_sum);
						}
				}
		}
				
        private void filter_image(int[] image, int im_height, int im_width, int[] filter, int f_size, int[] out) {
				// Applies filter to image data, puts filtered image data to out[]
                int f_sum = 0;
                int p_sum;
                int p_val;
                int edge = f_size/2;

                for (int i = 0; i<f_size*f_size; i++) {
                        f_sum += filter[i];
                }

                for (int row = 0; row < im_height; row++) {
                        for (int col = 0; col < im_width; col++) {
                                p_sum = 0;
                                for (int i = 0; i<f_size; i++) {
                                        for (int j = 0; j<f_size; j++) {
                                                if (row-edge+i < 0 || row-edge+i >= im_height) {
                                                        p_val = 0xffffffff;
                                                } else if (col-edge+j < 0 || col-edge+j >= im_width) {
                                                        p_val = 0xffffffff;
                                                } else {
                                                        p_val = image[(row-edge+i)*im_width + (col-edge+j)];
                                                }
                                                p_sum += get_grey(p_val) * filter[i*f_size + j];
                                        }
                                }
                                out[row*im_width + col] = put_grey(p_sum/f_sum);
                        }
                }
        }

        private void white_mask(int[] src, int im_height, int im_width, int[] mask, int[] dest) {
				// Takes source image data and black/white mask, puts max(source,mask) value to dest
				// Dest contains source image data where mask is black, white where mask is white
                for (int row = 0; row < im_height; row++) {
                        for (int col = 0; col < im_width; col++) {
                                if (get_grey(src[row*im_width + col]) > get_grey(mask[row*im_width + col])) {
                                        dest[row*im_width + col] = src[row*im_width + col];
                                } else {
                                        dest[row*im_width + col] = mask[row*im_width + col];
                                }
                        }
                }
        }

        private void convert_bw(int[] image, int[] out, int split) {
				// Takes image data, returns black/white image data
				// Optional parameter 0 < split < 255 threshold value
                int greyval;
                for (int i = 0; i<image.length; i++) {
                        greyval = get_grey(image[i]);
                        if (greyval >= split) {
                                out[i] = 0xffffffff;
                        }
                        else {
                                out[i] = 0xff000000;
                        }
                }
        }

        private void convert_bw(int[] image, int[] out) {
				// Runs convert_bw with default split parameter
                convert_bw(image, out, 128);
        }

        public Bitmap blob_cut(Bitmap input_image) {
				// Takes input bitmap image, returns image with blobs separated
                int[] input_data;
                int[] temp1;
                int[] temp2;
                int[] filter;
				int[] col_filter;
                int f_size;

                int im_height = input_image.getHeight();
                int im_width = input_image.getWidth();
                input_data = new int[im_height * im_width];

                //f_size = im_height/5 + 1 - (im_height/5)%2;
                f_size = 25;
                filter = new int[f_size * f_size];
				col_filter = new int[f_size];
                for (int i=0; i<f_size; i++) {
                        for (int j=0; j<f_size/2; j++) {
                                filter[i*f_size + j] = 0;
                        }
                        filter[i*f_size + f_size/2] = 1;
						col_filter[i] = 1;
                        for (int j=f_size/2 + 1; j<f_size; j++) {
                                filter[i*f_size + j] = 0;
                        }
                }

                input_image.getPixels(input_data, 0, im_width, 0, 0, im_width, im_height);

                temp1 = new int[im_height * im_width];
                convert_bw(input_data, temp1);

                temp2 = new int[im_height * im_width];
				
                for(int i = 0; i < 10; i++) {
                        column_filter_image(temp1, im_height, im_width, col_filter, f_size, temp2);
                        column_filter_image(temp2, im_height, im_width, col_filter, f_size, temp1);
                }
                
                //filter_image(temp1, im_height, im_width, filter, f_size, temp2);
				column_filter_image(temp1, im_height, im_width, col_filter, f_size, temp2);

                convert_bw(temp2, temp1, 192);
                white_mask(input_data, im_height, im_width, temp1, temp2);

                return Bitmap.createBitmap(temp2, im_width, im_height, Bitmap.Config.valueOf("ARGB_8888"));
        }
}