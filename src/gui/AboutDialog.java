package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutDialog extends JDialog implements ActionListener {
    protected Window parent;

    /**
     * Create the dialog.
     */
    public AboutDialog(Window parent) {
        super(parent, "About", Dialog.ModalityType.DOCUMENT_MODAL);
        setResizable(false);

        this.parent = parent;

        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new GridLayout(1, 1));
        {
            String text = "<html><center><H2>About</H2></center><br>" +
                    "<center>Russian draughts is a variant of the game of draughts, with which " +
                    "almost everyone in Russia is familiar. In our country, as well as in almost " +
                    "all countries of the former USSR, this kind of draughts game is the most popular, " +
                    "and rightfully bears the name of the \"people's game\".<br></center></html>";
            JLabel lblNewLabel_1 = new JLabel(text);
            lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
            contentPanel.add(lblNewLabel_1);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                okButton.addActionListener(this);
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            close();
        }
    }

    protected void close() {
        this.setVisible(false);
        dispose();
    }

    public void showDialog() {
        pack();
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(this.parent);
        setVisible(true);
    }
}
