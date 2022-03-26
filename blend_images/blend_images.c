#include <stdio.h>
#include <stdlib.h>
#include "blend_images.h"

void main(int argc, char *argv[])
{
    // hardcoded parameters
    char *file1 = "lion.bmp";
    char *file2 = "flowers.bmp";
    float ratio = 0.5;
    char *outfile = "merged.bmp";
    // replace hardcoded parameters with actual ones if var = 1.
    int read_args = 0;

    // read program parameters if it is enabled
    if(read_args == 1 && argc == 5)
    {
        file1 = argv[1];
        file2 = argv[2];
        ratio = atof(argv[3]);
        outfile = argv[4];
    }
    
    // read image 1 (file headers and pixel data)
    FILE *img1;
    img1 = fopen(file1, "rb");
    // read file hearder
    BITMAPFILEHEADER bfh;
    fread(&bfh.bfType, sizeof(WORD), 1, img1);  // bfType
    fread(&bfh.bfSize, sizeof(DWORD), 1, img1);  // bfSize
    fread(&bfh.bfReserved1, sizeof(WORD), 1, img1);  // bfReserved1
    fread(&bfh.bfReserved2, sizeof(WORD), 1, img1);  // bfReserved2
    fread(&bfh.bfOffBits, sizeof(DWORD), 1, img1);  //bfOffBits
    // read info header
    BITMAPINFOHEADER bih;
    fread(&bih, sizeof(bih), 1, img1);
    // read pixel data
    BYTE data[bih.biSizeImage];
    fread(data, bih.biSizeImage, 1, img1);
    // close file after reading data
    fclose(img1);
    
    // read image 2 (file headers and pixel data)
    FILE *img2;
    img2 = fopen(file2, "rb");
    // read file hearder
    BITMAPFILEHEADER bfh2;
    fread(&bfh2.bfType, sizeof(WORD), 1, img2);  // bfType
    fread(&bfh2.bfSize, sizeof(DWORD), 1, img2);  // bfSize
    fread(&bfh2.bfReserved1, sizeof(WORD), 1, img2);  // bfReserved1
    fread(&bfh2.bfReserved2, sizeof(WORD), 1, img2);  // bfReserved2
    fread(&bfh2.bfOffBits, sizeof(DWORD), 1, img2);  //bfOffBits
    // read info header
    BITMAPINFOHEADER bih2;
    fread(&bih2, sizeof(bih2), 1, img2);
    // read pixel data
    BYTE data2[bih2.biSizeImage];
    fread(data2, bih2.biSizeImage, 1, img2);
    // close file after reading data
    fclose(img2);

    // if there's two images with the same resolution
    if(bih.biWidth == bih2.biWidth && bih.biHeight == bih2.biHeight)
    {
        // create data array for the merged pixels
        BYTE data_final[bih.biSizeImage];

        // calculate realwidth
        int realwidth = bih.biWidth*3;
        if(realwidth %4 != 0)
            realwidth = realwidth + 4 - realwidth %4;

        // iterate pixels from the first image
        for(int x = 0; x < bih.biWidth; x++)
        {
            for(int y = 0; y < bih.biHeight; y++)
            {
                // for each pixel, blend its rgb values
                char colors[3] = {'r', 'g', 'b'};
                for(int i = 0; i < 3; i++)
                {
                    BYTE c1 = get_color(data, realwidth, x, y, colors[i]);
                    BYTE c2 = get_color(data2, realwidth, x, y, colors[i]);
                    BYTE cc = c1*ratio + c2*(1-ratio);
                    data_final[x*3 + y*realwidth + i] = cc;
                }
            }
        }

        // write bmp to output file
        FILE *out;
        out = fopen(outfile, "wb");
        // write file header
        fwrite(&bfh.bfType, 2, 1, out);
        fwrite(&bfh.bfSize, 4, 1, out);
        fwrite(&bfh.bfReserved1, 2, 1, out);
        fwrite(&bfh.bfReserved2, 2, 1, out);
        fwrite(&bfh.bfOffBits, 4, 1, out);
        // write info header
        fwrite(&bih, sizeof(bih), 1, out);
        // write pixel data
        fwrite(data_final, bih.biSizeImage, 1, out);
        fclose(out);
    }
    else  // otherwise use bilinear interpolation
    {
        // determine which image is larger
        BITMAPINFOHEADER *larger = &bih;
        BYTE (*ldata)[bih.biSizeImage] = &data;
        if(bih.biWidth < bih2.biWidth)
        {
            larger = &bih2;
            ldata = &data2;
        }

        // also define the smaller image
        BITMAPINFOHEADER *smaller = &bih;
        BYTE (*sdata)[bih.biSizeImage] = &data;
        if(smaller == larger)
        {
            smaller = &bih2;
            sdata = &data2;
        }

        // create data array for the merged pixels
        BYTE data_final[larger->biSizeImage]; 

        // calculate realwidth
        int realwidth = larger->biWidth*3;
        if(realwidth %4 != 0)
            realwidth = realwidth + 4 - realwidth % 4;
        int realws = smaller->biWidth*3;
        if(realws % 4 != 0)
            realws = realws +4 - realws % 4;

        // iterate pixels from the larger image
        for(int x = 0; x < larger->biWidth; x++)
        {
            for(int y = 0; y < larger->biHeight; y++)
            {
                // for each pixel, blend its rgb values
                char colors[3] = {'r', 'g', 'b'};
                for(int i = 0; i < 3; i++)
                {
                    // calculate interpolation info for x and y
                    float xs = x * ((float) smaller->biWidth / (float) larger->biWidth);
                    int x1 = xs;
                    int x2 = x1 + 1;
                    float dx = xs - x1;

                    float ys = y * ((float) smaller->biHeight / (float) larger->biHeight);
                    int y1 = ys;
                    int y2 = y1 + 1;
                    float dy = ys - y1;

                    // interpolate between 4 pixels
                    BYTE clu = get_color(*sdata, realws, x1, y2, colors[i]);
                    BYTE cll = get_color(*sdata, realws, x1, y1, colors[i]);
                    // check if x2 is out of bounds
                    BYTE cru, crl;
                    if(x2 == smaller->biWidth)
                    {
                        x2 = x1;
                        cru = clu;
                        crl = cll;
                    }
                    else
                    {
                        cru = get_color(*sdata, realws, x2, y2, colors[i]);
                        crl = get_color(*sdata, realws, x2, y1, colors[i]);
                    }
                    BYTE c_left = clu * (1 - dy) + cll * dy;
                    BYTE c_right = cru * (1 - dy) + crl * dy;
                    BYTE c_result = c_left * (1 - dx) + c_right * dx;

                    // merge interpolated color with color from larger's pixel
                    BYTE c_larger = get_color(*ldata, realwidth, x, y, colors[i]);
                    BYTE cc;
                    if(larger == &bih)
                        cc = c_result * (1 - ratio) + c_larger * ratio;
                    else
                        cc = c_result * ratio + c_larger * (1 - ratio);
                    
                    // update merged image pixel data
                    data_final[x*3 + y*realwidth + i] = cc;
                }
            }
        }

        // write bmp to output file
        FILE *out;
        out = fopen(outfile, "wb");
        if(larger == &bih)
        {
            // write file header
            fwrite(&bfh.bfType, 2, 1, out);
            fwrite(&bfh.bfSize, 4, 1, out);
            fwrite(&bfh.bfReserved1, 2, 1, out);
            fwrite(&bfh.bfReserved2, 2, 1, out);
            fwrite(&bfh.bfOffBits, 4, 1, out);
            // write info header
            fwrite(&bih, sizeof(bih), 1, out);
            // write pixel data
            fwrite(data_final, bih.biSizeImage, 1, out);
        }
        else
        {
            // write file header
            fwrite(&bfh2.bfType, 2, 1, out);
            fwrite(&bfh2.bfSize, 4, 1, out);
            fwrite(&bfh2.bfReserved1, 2, 1, out);
            fwrite(&bfh2.bfReserved2, 2, 1, out);
            fwrite(&bfh2.bfOffBits, 4, 1, out);
            // write info header
            fwrite(&bih2, sizeof(bih2), 1, out);
            // write pixel data
            fwrite(data_final, bih2.biSizeImage, 1, out);
        }
        fclose(out);
    }
}

BYTE get_color(BYTE* data, int realw, int x, int y, char color)
{
    // color parameter should be one of three chars: 'r', 'g', 'b'.
    int offset = 0;
    if(color == 'g')
        offset = 1;
    else if(color == 'b')
        offset = 2;
    return data[x*3 + y*realw + offset];
}
