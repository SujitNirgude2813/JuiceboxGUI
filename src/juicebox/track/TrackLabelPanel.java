/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2021 Broad Institute, Aiden Lab, Rice University, Baylor College of Medicine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package juicebox.track;

import juicebox.JBGlobals;
import juicebox.data.HiC;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author jrobinso
 *         Date: 8/3/13
 *         Time: 9:36 PM
 */
public class TrackLabelPanel extends JPanel {

    private static final long serialVersionUID = 9000037;
    private final HiC hic;
    final private int leftMargin = 10;
    final private int rightMargin = 5;
	private HiCTrack eigenvectorTrack;
	private final int numExtraBufferLinesSpaces = 2;

    public TrackLabelPanel(HiC hic) {
        this.hic = hic;
        setLayout(null);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);

        for (Component c : this.getComponents()) {
            if (c instanceof JLabel) {
                Rectangle bounds = c.getBounds();
                bounds.width = width - (leftMargin + rightMargin);
                c.setBounds(bounds);
            }
        }
    }

    public void updateLabels() {

        removeAll();

        if (hic.getDataset() == null) {
            return;
        }

        java.util.List<HiCTrack> tracks = new ArrayList<>(hic.getLoadedTracks());
        int trackCount = tracks.size() + (eigenvectorTrack == null ? 0 : 1);
        if (trackCount == 0) {
            return;
        }

        int top = 0;
        final int width = getWidth() - (leftMargin + rightMargin);
        for (HiCTrack hicTrack : tracks) {
            JLabel textLabel = getTrackLabel(hicTrack.getName(), true);

            textLabel.setBounds(leftMargin, top, width, hicTrack.getHeight());
            textLabel.setBackground(Color.CYAN);
            add(textLabel);
            top += hicTrack.getHeight();
        }

    }

    private JLabel getTrackLabel(String name, boolean addToolTip) {
        JLabel label = new JLabel(name); //, SwingConstants.RIGHT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFont(JBGlobals.font(10, false));
        if (addToolTip)
            label.setToolTipText(name);
        return label;
    }
}
