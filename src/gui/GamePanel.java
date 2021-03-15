package gui;

import basis.Round;
import game.Game;
import replay.GameReplay;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends JPanel {
    protected Game game;
    protected GameBoard gameBoard;
    protected JList<Round> logList;
    protected String name;

    /**
     * Create the panel.
     */
    public GamePanel(Game game) {
        setBorder(new EmptyBorder(0, 1, 0, 0));
        this.game = game;
        this.setLayout(new BorderLayout());
        this.gameBoard = new GameBoard(this.game);
        this.logList = createLogList();
        this.add(gameBoard, BorderLayout.CENTER);
        this.add(createScrollBar(this.logList), BorderLayout.EAST);
        this.setVisible(false);
        refresh();
    }

    protected JList<Round> createLogList() {
        DefaultListModel<Round> logModel = new DefaultListModel<>();
        JList<Round> logList = new JList<>(logModel);
        logList.setBorder(new EmptyBorder(0, 0, 0, 0));
        logList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return logList;
    }

    protected JScrollPane createScrollBar(JList<Round> list) {
        JScrollPane sp = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(new Dimension(120, this.getHeight()));
        return sp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GamePanel) {
            GamePanel gp = (GamePanel) obj;
            return this.name.equals(gp.name);
        }
        return false;
    }

    public Game getGame() {
        return this.game;
    }

    public GameBoard getGameBoard() {
        return this.gameBoard;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void refresh() {
        this.gameBoard.repaint();
        refreshLog();
    }

    private void refreshLog() {
        if (this.game == null) {
            return;
        }

        java.util.List<Round> rounds = this.game.getRounds();
        DefaultListModel<Round> logModel = (DefaultListModel<Round>) (logList
                .getModel());

        logModel.clear();

        for (Round round : rounds) {
            logModel.addElement(round);
        }

        if (this.game instanceof GameReplay) {
            GameReplay gr = (GameReplay) this.game;
            int index = gr.getActualStepIndex() / 2;
            this.logList.setSelectedIndex(index);
        }

        this.logList.revalidate();
    }

    public void setGame(Game game) {
        this.game = game;
        this.gameBoard.setGame(game);
        refresh();
    }

    public void setInteractivity(boolean mode) {
        if (this.gameBoard != null) {
            this.gameBoard.setInteractivity(mode);
        }
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
