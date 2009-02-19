/*
 *  Copyright 2000-2007 JetBrains s.r.o.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.maddyhome.idea.copyright.actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecursionDlg extends DialogWrapper
{
    public RecursionDlg(Project project, VirtualFile file)
    {
        super(project, false);

        logger.debug("file=" + file);

        this.project = project;
        this.file = file;

        setupControls();

        init();
    }

    public boolean isAll()
    {
        return rbAll.isSelected();
    }

    public boolean includeSubdirs()
    {
        return cbSubdirs.isSelected();
    }

    private void setupControls()
    {
        setTitle("Update Copyright");

        setOKButtonText("Run");

        ButtonGroup group = new ButtonGroup();
        group.add(rbFile);
        group.add(rbAll);

        rbFile.setMnemonic('F');
        rbAll.setMnemonic('A');
        cbSubdirs.setMnemonic('I');

        if (file.isDirectory())
        {
            rbFile.setText("File");
            rbFile.setEnabled(false);
            rbAll.setText("All files in " + file.getPresentableUrl());
            rbAll.setSelected(true);
            cbSubdirs.setSelected(true);
            cbSubdirs.setEnabled(true);
        }
        else
        {
            VirtualFile parent = file.getParent();
            rbFile.setText("File '" + file.getPresentableUrl() + '\'');
            rbFile.setSelected(true);
            if (parent != null)
            {
                rbAll.setText("All files in " + parent.getPresentableUrl());
                cbSubdirs.setSelected(true);
                cbSubdirs.setEnabled(false);
            }
            else
            {
                rbAll.setVisible(false);
                cbSubdirs.setVisible(false);
            }
        }

        VirtualFile check = file;
        if (!file.isDirectory())
        {
            check = file.getParent();
        }
        ProjectRootManager prm = ProjectRootManager.getInstance(project);
        ProjectFileIndex pfi = prm.getFileIndex();

        VirtualFile[] children = check != null ? check.getChildren() : VirtualFile.EMPTY_ARRAY;
        boolean hasSubdirs = false;
        for (int i = 0; i < children.length && !hasSubdirs; i++)
        {
            if (children[i].isDirectory() && !pfi.isIgnored(children[i]))
            {
                hasSubdirs = true;
            }
        }

        cbSubdirs.setVisible(hasSubdirs);
        if (!hasSubdirs)
        {
            cbSubdirs.setEnabled(false);
            mainPanel.remove(cbSubdirs);
        }

        ActionListener listener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (cbSubdirs.isVisible())
                {
                    cbSubdirs.setEnabled(rbAll.isSelected());
                }
            }
        };

        rbFile.addActionListener(listener);
        rbAll.addActionListener(listener);
    }

    protected JComponent createCenterPanel()
    {
        return mainPanel;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your
     * code!
     */
    private void $$$setupUI$$$()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        rbFile = new JRadioButton();
        rbFile.setText("File");
        mainPanel.add(rbFile, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        rbAll = new JRadioButton();
        rbAll.setText("All");
        mainPanel.add(rbAll, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        cbSubdirs = new JCheckBox();
        cbSubdirs.setText("Include subdirectories");
        mainPanel.add(cbSubdirs, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("  ");
        mainPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null));
    }

    private final VirtualFile file;
    private JPanel mainPanel;
    private JRadioButton rbFile;
    private JRadioButton rbAll;
    private JCheckBox cbSubdirs;
    private final Project project;

    private static final Logger logger = Logger.getInstance(RecursionDlg.class.getName());

}