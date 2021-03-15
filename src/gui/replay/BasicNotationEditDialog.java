package gui.replay;

import game.Game;
import log.BasicNotationLog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasicNotationEditDialog extends JDialog implements ActionListener {
    private final JPanel contentPanel = new JPanel();
    protected Game game;
    protected Window parent;
    protected Game result;
    protected JTextArea textArea;

    /**
     * Create the dialog.
     */
    public BasicNotationEditDialog(Window parent, Game game) {
        super(parent, "Replay", Dialog.ModalityType.DOCUMENT_MODAL);
        this.parent = parent;
        this.game = game;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initComponents();
        this.textArea.setText(openLog(this.game));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("OK".equals(e.getActionCommand())) {
            if (saveLog()) {
                close();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Can not save: syntax error.", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        } else if ("Cancel".equals(e.getActionCommand())) {
            close();
        }
    }

    public void close() {
        setVisible(false);
    }

    protected void initComponents() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        {
            JScrollPane scrollPane = new JScrollPane();
            contentPanel.add(scrollPane);
            {
                textArea = new JTextArea();
                scrollPane.setViewportView(textArea);
            }
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
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(this);
                buttonPane.add(cancelButton);
            }
        }
    }

    public String openLog(Game game) {
        BasicNotationLog notation = new BasicNotationLog();
        return notation.getLog(game);
    }

    public Game parseLog(Game game, String input) {
        BasicNotationLog notation = new BasicNotationLog();
        return notation.setLog(game, input);
    }

    public boolean saveLog() {
        Game newGame = parseLog(this.game, this.textArea.getText());
        this.result = newGame;
        return newGame != null;
    }

    public Game showDialog() {
        pack();
        setBounds(100, 100, 450, 300);
        setLocationRelativeTo(this.parent);
        setVisible(true);
        return this.result;
    }
}
