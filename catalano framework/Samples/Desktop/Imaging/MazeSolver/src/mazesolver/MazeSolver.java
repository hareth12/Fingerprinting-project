// Maze Solver, free solution for maze using image processing.
//
// Copyright © Diego Catalano, 2013
// diego.catalano at live.com
//
//    This program is free software; you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation; either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program; if not, write to the Free Software
//    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//

package mazesolver;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Dilatation;
import Catalano.Imaging.Filters.Erosion;
import Catalano.Imaging.Filters.FloodFill;
import Catalano.Imaging.Filters.ReplaceColor;
import Catalano.Imaging.Filters.Threshold;
import java.awt.Color;

/**
 * Solve maze using dilatation and erosion.
 * @author Diego Catalano
 */
public class MazeSolver {
    
    private int sizeDilation = 4;
    private int sizeErosion = 2;
    private Color color = Color.red;
    FastBitmap original;
    
    /**
     * Initializes a new instance of the MazeSolver class.
     */
    public MazeSolver(){}

    /**
     * Initializes a new instance of the MazeSolver class.
     * @param sizeDilation Kernel size for dilation.
     * @param sizeErosion Kernel size for erosion.
     */
    public MazeSolver(int sizeDilation, int sizeErosion) {
        this.sizeDilation = sizeDilation;
        this.sizeErosion = sizeErosion;
    }
    
    /**
     * Initializes a new instance of the MazeSolver class.
     * @param sizeDilation Kernel size for dilation.
     * @param sizeErosion Kernel size for erosion.
     * @param color Color for show the path.
     */
    public MazeSolver(int sizeDilation, int sizeErosion, Color color) {
        this.sizeDilation = sizeDilation;
        this.sizeErosion = sizeErosion;
        this.color = color;
    }
    
    /**
     * Solve the maze.
     * @param fastBitmap Image to be processed.
     * @return Image processed.
     */
    public FastBitmap Solve(FastBitmap fastBitmap){
        
        //Pre-process image
            // Convert to rgb if the image is gray or argb.
            fastBitmap.toRGB();
            // Convert to gray
            fastBitmap.toGrayscale();
            // Apply threshold for eliminate false positive gray pixels.
            Threshold t = new Threshold();
            t.applyInPlace(fastBitmap);
            // Back to RGB
            fastBitmap.toRGB();
        
            original = new FastBitmap(fastBitmap);
        
        //First, we need to find a wall and colorize with pre-defined color
            int width = fastBitmap.getWidth();
            int height = fastBitmap.getHeight();
        
            boolean start = true;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    // Find the first pixel black, what means the wall.
                    if (fastBitmap.getRed(i, j) == 0 && start){
                        FloodFill ff = new FloodFill(i, j, color.getRed(), color.getGreen(), color.getBlue());
                        ff.applyInPlace(fastBitmap);
                        start = false;
                    }
                }
            }
        
        //Second, filter image with color
        ReplaceColor r = new ReplaceColor(255, 255, 255);
        r.ApplyInPlace(fastBitmap, 0, 0, 0);
        
        fastBitmap.toGrayscale();
        
        //Create a copy and apply dilatation
        FastBitmap copyDil = new FastBitmap(fastBitmap);
        Dilatation dil = new Dilatation(sizeDilation);
        dil.applyInPlace(copyDil);
        
        //Create a second copy and apply erosion
        FastBitmap copyEro = new FastBitmap(copyDil);
        Erosion ero = new Erosion(sizeErosion);
        ero.applyInPlace(copyEro);
        
        //Threshold the both copy
        t.setValue(10);
        t.applyInPlace(copyDil);
        t.applyInPlace(copyEro);
        
        //Verify and apply color
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (copyDil.getGray(i, j) == 255 && copyEro.getGray(i, j) == 0)
                    original.setRGB(i, j, color.getRed(), color.getGreen(), color.getBlue());
            }
        }
               
        return original;
        
    }
    
}
