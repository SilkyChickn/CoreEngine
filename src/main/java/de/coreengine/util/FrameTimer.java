/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Suuirad
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.coreengine.util;

/**
 * Class that calculates the time since last second and the fps
 *
 * @author Darius Dinger
 */
public class FrameTimer {

    // Time since last frame
    private static float tslf = 0.017f;

    // Last second fps
    private static int fps;

    // Should smooth fps be used
    private static boolean smoothFps = true;

    // Current, last frame nd fps counter time stamps
    private static long lastFrame, fpsStamp;

    // Frames since last second
    private static int frames;

    /**
     * Recalculate the fps and tslf. Must be called once at every frame
     */
    public static void update() {

        frames++;

        long currentFrame = System.nanoTime();
        tslf = (float) (currentFrame - lastFrame) / 1000000000.0f;
        lastFrame = currentFrame;

        if (currentFrame >= fpsStamp + 1000000000.0f) {
            fpsStamp = currentFrame;

            if (smoothFps && fps != 0)
                fps = (fps + frames) / 2;
            else
                fps = frames;

            frames = 0;

            // System.out.println("FPS: " + getFps());
        }

    }

    /**
     * @return Frames in the last second
     */
    public static int getFps() {
        return fps;
    }

    /**
     * @return Time (seconds) since last frame
     */
    public static float getTslf() {
        return tslf;
    }

    /**
     * If smooth fps is enabled, the fps will be calculated by the average of the
     * last fps (fps = (fps + newFps) / 2)
     * 
     * @param smoothFps Should be used smooth fps
     */
    public static void setSmoothFps(boolean smoothFps) {
        FrameTimer.smoothFps = smoothFps;
    }
}
