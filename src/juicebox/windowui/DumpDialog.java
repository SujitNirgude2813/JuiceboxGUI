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


package juicebox.windowui;

import javastraw.reader.expected.ExpectedValueFunction;
import javastraw.reader.norm.NormalizationVector;
import javastraw.reader.type.MatrixType;
import javastraw.reader.type.NormalizationHandler;
import juicebox.DirectoryManager;
import juicebox.HiC;
import juicebox.MainWindow;
import juicebox.data.GUIMatrixZoomData;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

public class DumpDialog extends JFileChooser {

    private static final long serialVersionUID = 900009;
    private JComboBox<String> box;

    /**
     * TODO I think a good amount of the code below is duplicated in the dumpGeneralVector method and should call that instead
     *
     * @param mainWindow
     * @param hic
     */
    public DumpDialog(MainWindow mainWindow, HiC hic) {
        super();
        int result = showSaveDialog(mainWindow);
        if (result == JFileChooser.APPROVE_OPTION) {
            GUIMatrixZoomData zd;
            try {
                zd = hic.getZd();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "ZoomData error while writing", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (box.getSelectedItem().equals("Matrix")) {
                    ExpectedValueFunction df = null;
                    MatrixType matrixType = hic.getDisplayOption();
                    if (MatrixType.isExpectedValueType(matrixType)) {
                        df = hic.getDataset().getExpectedValues(zd.getZoom(), hic.getObsNormalizationType(), true);
                        if (df == null) {
                            JOptionPane.showMessageDialog(this, box.getSelectedItem() + " not available", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    zd.dump(new PrintWriter(getSelectedFile()), hic.getObsNormalizationType(), matrixType,
                            hic.getCurrentRegionWindowGenomicPositions(), df);

                } else if (box.getSelectedItem().equals("Norm vector")) {

                    if (hic.getObsNormalizationType().equals(NormalizationHandler.NONE)) {
                        JOptionPane.showMessageDialog(this, "Selected normalization is None, nothing to write",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        NormalizationVector nv = hic.getNormalizationVector(zd.getChr1Idx());
                        PrintWriter pw = new PrintWriter(getSelectedFile());
                        // print out vector
                        for (double[] array : nv.getData().getValues()) {
                            for (double element : array) {
                                pw.println(element);
                            }
                        }
                        pw.close();
                    }
                } else if (box.getSelectedItem().toString().contains("Expected")) {

                    final ExpectedValueFunction df = hic.getDataset().getExpectedValues(zd.getZoom(),
                            hic.getObsNormalizationType(), true);
                    if (df == null) {
                        JOptionPane.showMessageDialog(this, box.getSelectedItem() + " not available", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (box.getSelectedItem().equals("Expected vector")) {
                        long length = df.getLength();
                        int c = zd.getChr1Idx();
                        PrintWriter pw = new PrintWriter(getSelectedFile());
                        for (long i = 0; i < length; i++) {
                            pw.println((float) df.getExpectedValue(c, i));
                        }
                        pw.flush();
                    } else {
                        PrintWriter pw = new PrintWriter(getSelectedFile());
                        // print out vector
                        for (double[] values : df.getExpectedValuesNoNormalization().getValues()) {
                            for (double element : values) {
                                pw.println(element);
                            }
                        }
                        pw.close();
                    }
                }
            } catch (IOException error) {
                JOptionPane.showMessageDialog(this, "Error while writing:\n" + error, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected JDialog createDialog(Component component) throws HeadlessException {
        JDialog dialog = super.createDialog(component);
        JPanel panel1 = new JPanel();
        JLabel label = new JLabel("Dump ");
        box = new JComboBox<>(new String[]{"Matrix", "Norm vector", "Expected vector", "Expected genome-wide vector"});
        panel1.add(label);
        panel1.add(box);
        dialog.add(panel1, BorderLayout.NORTH);
        setCurrentDirectory(DirectoryManager.getHiCDirectory());
        setDialogTitle("Choose location for dump of matrix or vector");
        setFileSelectionMode(JFileChooser.FILES_ONLY);
        return dialog;
    }

}
