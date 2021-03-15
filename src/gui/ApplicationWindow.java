package gui;

import basis.Step;
import game.Game;
import game.Game.StepStatus;
import game.Player;
import gui.GuiUpdate.GuiUpdateListener;
import gui.replay.ReplayDialog;
import gui.replay.ReplyListener;
import log.BasicNotationLog;
import log.IFileLog;
import log.XMLLog;
import players.ComputerPlayer;
import players.HumanPlayer;
import players.NetworkPlayer.NetworkPlayer;
import replay.GameReplay;
import replay.Replay;
import replay.Replay.ReplayStatus;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ApplicationWindow extends JFrame implements ActionListener,
        GuiUpdateListener, ReplyListener {

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        ApplicationWindow frame = new ApplicationWindow();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void run() {
        javax.swing.SwingUtilities.invokeLater(ApplicationWindow::createAndShowGUI);
    }

    protected JPanel containerPanel = new JPanel();
    protected JFileChooser fc;
    protected Game game;
    protected JMenu gameMenu;
    protected GamePanel gamePanel;
    protected Replay replay;
    protected JMenuItem replayStartMenuItem;
    protected JMenuItem replayStopMenuItem;
    protected File saveFile;

    public ApplicationWindow() {
        super("Checkers");
        setBounds(0, 0, 1024, 768);

        Image windowIcon = loadImage("/checkers.png");
        setIconImage(windowIcon);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        this.containerPanel.setLayout(new BorderLayout());
        getContentPane().add(containerPanel, BorderLayout.CENTER);
        setJMenuBar(createMenuBar());

        JPanel statusBar = createStatusBar();
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        this.fc = createFileChooser();
        this.replay = new Replay();
        ReplayDialog rd = new ReplayDialog(this, this.replay);

        this.replay.addListener(this);
        this.replay.addListener(rd);
        this.replay.init();

        setStatusBar("Select \"Game -> New\" to start new game.");
        createGame(new ComputerPlayer(false));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("quit".equals(e.getActionCommand())) {
            exit();
        } else if ("about".equals(e.getActionCommand())) {
            showAbout();
        } else if ("new_human".equals(e.getActionCommand())) {
            createGame(new HumanPlayer(false));
        } else if ("new_computer".equals(e.getActionCommand())) {
            createGame(new ComputerPlayer(false));
        } else if ("new_network".equals(e.getActionCommand())) {
            createNetworkGame();
        } else if ("close".equals(e.getActionCommand())) {
            confirmClose();
        } else if ("save".equals(e.getActionCommand())) {
            saveGame();
        } else if ("save_as".equals(e.getActionCommand())) {
            saveAsGame();
        } else if ("open".equals(e.getActionCommand())) {
            openGame();
        } else if ("hint".equals(e.getActionCommand())) {
            showHint();
        } else if ("replay_start".equals(e.getActionCommand())) {
            setReplayMode(true);
        } else if ("replay_stop".equals(e.getActionCommand())) {
            setReplayMode(false);
        }
    }

    protected void closeGame() {
        if (this.gamePanel != null) {
            if (this.game != null) {
                this.game.dispose();
            }

            this.game = null;
            this.gamePanel = null;
            this.saveFile = null;

            setGamePanel(null);
            setStatusBar("Game closed.");
            setPlayerBar("");
            this.setTitle("Game closed.");
            this.repaint();
        }
    }

    protected boolean confirmClose() {
        if (confirmSave()) {
            closeGame();
            return true;
        }

        return false;
    }

    protected boolean confirmSave() {
        if (this.game != null && this.game.isDirty()) {
            int n = JOptionPane.showOptionDialog(this,
                    "Do you want to save the game before closing?",
                    "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (n == JOptionPane.YES_OPTION) {
                saveGame();
                closeGame();
                return true;
            } else return n == 1;
        }

        return true;
    }

    protected JFileChooser createFileChooser() {
        JFileChooser fc = new JFileChooser("../examples");
        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xml")
                        || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Checker XML files";
            }
        });

        fc.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".dat")
                        || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Checker specific dat files";
            }
        });

        return fc;
    }

    protected void createGame(Player player2) {
        if (!confirmClose()) {
            return;
        }

        startGame(new Game(new HumanPlayer(player2.isBlack()), player2));
    }


    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu game = new JMenu("Game");
        game.setName("game");
        menuBar.add(game);
        gameMenu = game;

        JMenu newGame = new JMenu("New");
        game.add(newGame);

        JMenuItem humanmode = new JMenuItem("Human");
        humanmode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
        humanmode.setActionCommand("new_human");
        humanmode.addActionListener(this);
        newGame.add(humanmode);

        JMenuItem computermode = new JMenuItem("Computer");
        computermode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        computermode.setActionCommand("new_computer");
        computermode.addActionListener(this);
        newGame.add(computermode);

        JMenuItem networkmode = new JMenuItem("Network");
        networkmode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK));
        networkmode.setActionCommand("new_network");
        networkmode.addActionListener(this);
        newGame.add(networkmode);

        JMenu replayGame = new JMenu("Replay");
        game.add(replayGame);

        JMenuItem startGame = new JMenuItem("Start");
        startGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        startGame.setActionCommand("replay_start");
        startGame.addActionListener(this);
        startGame.setName("replay_start");
        replayStartMenuItem = startGame;
        replayGame.add(startGame);

        JMenuItem stopGame = new JMenuItem("Stop");
        stopGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.ALT_MASK));
        stopGame.setActionCommand("replay_stop");
        stopGame.addActionListener(this);
        stopGame.setName("replay_stop");
        replayStopMenuItem = stopGame;
        replayGame.add(stopGame);

        JMenuItem openGame = new JMenuItem("Open");
        openGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
        openGame.setActionCommand("open");
        openGame.addActionListener(this);
        game.add(openGame);

        JMenuItem saveGame = new JMenuItem("Save");
        saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK));
        saveGame.setActionCommand("save");
        saveGame.addActionListener(this);
        game.add(saveGame);

        JMenuItem menuItem = new JMenuItem("Save as");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
        menuItem.setActionCommand("save_as");
        menuItem.addActionListener(this);
        game.add(menuItem);

        menuItem = new JMenuItem("Close");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        menuItem.setActionCommand("close");
        menuItem.addActionListener(this);
        game.add(menuItem);

        menuItem = new JMenuItem("Hint");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
        menuItem.setActionCommand("hint");
        menuItem.addActionListener(this);
        game.add(menuItem);

        menuItem = new JMenuItem("About");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK));
        menuItem.setActionCommand("about");
        menuItem.addActionListener(this);
        game.add(menuItem);

        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_MASK));
        menuItem.setActionCommand("quit");
        menuItem.addActionListener(this);
        game.add(menuItem);
        return menuBar;
    }

    public static BufferedImage loadImage(String pathImage) {
        try {
            return ImageIO.read(ApplicationWindow.class.getResource(pathImage));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    protected void createNetworkGame() {
        if (!confirmClose()) {
            return;
        }

        NetworkGameDialog ngd = new NetworkGameDialog(this);
        Game game = ngd.showDialog();

        if (game != null) {
            startGame(game);
        }
    }

    protected JPanel createStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setName("statusPanel");

        statusPanel
                .setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

        statusPanel.setPreferredSize(new Dimension(this.getWidth(), 40));
        statusPanel.setLayout(new GridLayout(0, 2, 0, 0));

        JLabel statusLabel = new JLabel("status");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        statusLabel.setName("statusLabel");
        statusPanel.add(statusLabel);

        JLabel playerLabel = new JLabel("");
        playerLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        playerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        playerLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        playerLabel.setName("playerLabel");
        statusPanel.add(playerLabel);
        return statusPanel;
    }

    public void exit() {
        if (confirmClose()) {
            this.dispose();
        }
    }

    protected void openGame() {
        if (!confirmClose()) {
            return;
        }

        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fc.getSelectedFile();
        IFileLog fileLog = file.getName().toLowerCase().endsWith(".dat") ? new BasicNotationLog()
                : (file.getName().toLowerCase().endsWith(".xml") ? new XMLLog()
                : new BasicNotationLog());

        Game newGame = Game.open(file, fileLog);

        if (newGame != null) {
            startGame(newGame);
            this.saveFile = file;
            setStatusBar("Game opened.");

            if (newGame.getPlayer2() instanceof NetworkPlayer) {
                showWarningDialog(
                        "You can open a network game file, but can not continue playing.",
                        "Warning");
                setPlayerBar("");
            }
        } else {
            setStatusBar("Game can not be opened.");
        }
    }

    @Override
    public void refreshGui() {
        if (this.gamePanel != null) {
            this.gamePanel.refresh();
        }

        if (this.game != null) {
            Player player = this.game.getCurrentPlayer();

            if (player != null) {
                setPlayerBar(player.toString() + " player's turn");
            } else {
                setPlayerBar("");
            }

            String message = "";

            StepStatus status = this.game.getLastStepStatus();

            if (status != null) {
                switch (status) {
                    case POS_FIG_NULL:
                        message = "Can not move this position.";
                        break;
                    case NO_FIGURE:
                        message = "There is no figure at position.";
                        break;
                    case NOT_CURRENT_PLAYER:
                        message = "You are not the current player.";
                        break;
                    case CANT_MOVE:
                        message = "This figure can not move to this position.";
                        break;
                    case MUST_MOVE:
                        message = "There is a move you must do!";
                        break;
                    case NOT_DEFINED:
                        message = "Unknonwn error.";
                        break;
                    case OK:
                        Step lastStep = this.game.getLastStep();
                        if (lastStep != null)
                            message = "Last step: " + lastStep.toString();
                        break;
                }
            }

            if (player instanceof NetworkPlayer) {
                NetworkPlayer np = (NetworkPlayer) player;
                String errorMessage = np.getLastError();

                if (errorMessage != null && !errorMessage.equals("")) {
                    message = errorMessage;
                    game.setFinished(true);
                }
            }

            if (this.game.getFinished()) {
                Player winner = this.game.getWinner();
                message = "Finished: " + winner.toString() + " won.";
                setPlayerBar("");
                showInfoDialog(message, "Information");
            }

            setStatusBar(message);
            this.setTitle(game.toString());
        } else {
            setStatusBar("");
            setPlayerBar("");
        }
    }

    @Override
    public void replayModeEnded() {
        setGamePanel(this.gamePanel);
        setStatusBar("Replay Mode has been turned off.");
        setPlayerBar("");
    }

    @Override
    public void replayModeStarted() {
        setGamePanel(this.replay.getReplayPanel());
        setPlayerBar("Replay Mode ON");
    }

    @Override
    public void replayRefreshGui() {
        boolean active = this.replay.isActive();

        gameMenu.setEnabled(!active);
        replayStartMenuItem.setEnabled(!active);
        replayStopMenuItem.setEnabled(active);

        GameReplay gr = this.replay.getSelectedGame();

        String status = "";

        if (gr != null) {
            Step s = gr.getActualStep();
            int stepCount = gr.getStepCount();
            int actualStepIndex = gr.getActualStepIndex() + 1;

            status = "Last step: " + (s != null ? s.toString() : "<none>");

            if (s != null) {
                status += " [" + actualStepIndex + "/" + stepCount + "]";
            }
        }

        if (this.replay.isStepping()) {
            status = "[Stepping] " + status;
        }

        ReplayStatus rs = this.replay.getStatus();

        if (rs == ReplayStatus.CANNOT_OPEN) {
            status = "Can not open file: can not understand format.";
        }

        this.replay.displayedStatus();

        setStatusBar(status);

        this.getContentPane().repaint();
    }

    protected void saveAsGame() {
        if (this.game != null
                && fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.saveFile = fc.getSelectedFile();
            IFileLog fileLog = this.saveFile.getName().toLowerCase()
                    .endsWith(".dat") ? new BasicNotationLog() : (this.saveFile
                    .getName().toLowerCase().endsWith(".xml") ? new XMLLog()
                    : new BasicNotationLog());
            this.game.setFileType(fileLog);
            saveGame();
        }
    }

    protected void saveGame() {
        if (this.saveFile != null) {
            if (this.game != null && this.game.save(this.saveFile)) {
                setStatusBar("Saved!");
            } else {
                setStatusBar("Not saved!");
            }
        } else {
            saveAsGame();
        }
    }

    protected void setGamePanel(GamePanel gp) {
        this.containerPanel.removeAll();

        if (gp == null) {
            return;
        }

        this.containerPanel.add(gp, BorderLayout.CENTER);
        gp.setVisible(true);
        this.getContentPane().repaint();
    }

    protected void setMessagePanel(String message, String dest) {
        for (Component component : this.getContentPane().getComponents()) {
            if (component.getName() == "statusPanel") {
                JLabel statusLabel = (JLabel) ((JPanel) component)
                        .getComponent(0);
                statusLabel.setText(message);
            }
        }
    }

    public void setPlayerBar(String message) {
        for (Component component : this.getContentPane().getComponents()) {
            if (component.getName() == "statusPanel") {
                JLabel statusLabel = (JLabel) ((JPanel) component)
                        .getComponent(1);
                statusLabel.setText(message);
            }
        }
    }

    protected void setReplayMode(boolean mode) {
        if (mode) {
            if (this.game != null) {
                this.replay.addGame(this.game);
            }
            this.replay.startReplayMode();
        } else {
            this.replay.stopReplayMode();
        }
    }

    public void setStatusBar(String message) {
        for (Component component : this.getContentPane().getComponents()) {
            if (component.getName() == "statusPanel") {
                JLabel statusLabel = (JLabel) ((JPanel) component)
                        .getComponent(0);
                statusLabel.setText(message);
            }
        }
    }

    protected void showAbout() {
        AboutDialog about = new AboutDialog(this);
        about.showDialog();
    }

    public void showHint() {
        if (this.game != null) {
            ComputerPlayer player = new ComputerPlayer(this.game
                    .getCurrentPlayer().isWhite());
            java.util.List<Step> steps = player.getAllStep(this.game);

            StringBuilder message = new StringBuilder("You can make the following step(s): \n");

            for (Step step : steps) {
                message.append(step.toString()).append("\n");
            }

            showInfoDialog(message.toString(), "Hint");
        }
    }

    public void showInfoDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWarningDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    protected void startGame(Game game) {
        this.game = game;

        game.addListener(this);
        game.setFileType(new BasicNotationLog());

        gamePanel = new GamePanel(game);
        gamePanel.setName("gamepanel");
        gamePanel.setGame(game);

        this.game.getPlayer1().addListener(this);
        this.game.getPlayer2().addListener(this);

        setGamePanel(gamePanel);

        game.start();

        setStatusBar("New game started.");
        this.setTitle(game.toString());
    }

    public static void main(String[] args) {
        ApplicationWindow.run();
    }
}
