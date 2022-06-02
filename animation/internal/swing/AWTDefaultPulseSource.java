/*
 * Copyright (c) 2005-2022 Radiance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package animation.internal.swing;

import animation.api.RadianceAnimationCortex;

import java.awt.*;
import java.util.Arrays;

/**
 * Default pulse source which derives the pulse delay from
 * the highest refresh rate of all local screen devices.
 *
 * @author Timotej Šulík.
 */
public class AWTDefaultPulseSource extends RadianceAnimationCortex.FixedRatePulseSource {
    /**
     * Refresh rate to use if none of the screen devices report a valid refresh rate.
     */
    private static final int REFRESH_RATE_FALLBACK = 60;

    public AWTDefaultPulseSource() {
        super(1000 / getHighestDisplayRefreshRate());
    }

    private static int getHighestDisplayRefreshRate() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return Arrays.stream(ge.getScreenDevices())
                .mapToInt(rate -> rate.getDisplayMode().getRefreshRate())
                .filter(rate -> rate != DisplayMode.REFRESH_RATE_UNKNOWN)
                .max()
                .orElse(REFRESH_RATE_FALLBACK);
    }
}