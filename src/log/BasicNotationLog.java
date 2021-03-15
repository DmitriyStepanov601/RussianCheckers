package log;

import basis.Round;
import game.Game;
import game.Game.StepStatus;
import game.Player;
import players.ComputerPlayer;
import players.HumanPlayer;
import players.NetworkPlayer.NetworkPlayer;

import java.io.*;
import java.util.List;

public class BasicNotationLog implements IFileLog {
    public BasicNotationLog() {

    }

    public String getLog(Game game) {
        String result = "";

        if (game == null) {
            return result;
        }

        return parseRounds(game.getRounds());
    }

    @Override
    public Game open(File file) {
        BufferedReader br = null;
        Game game = null;
        Player tmpPlayer = null;

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            int flag = 0;

            while ((sCurrentLine = br.readLine()) != null) {

                if (flag > 1) {
                    // steps
                    Round round = Round.parseRound(sCurrentLine, game);
                    assert game != null;
                    StepStatus status = game.addRound(round);
                            
                    if (status != StepStatus.OK) {
                        return null;
                    }
                } else {
                    // init
                    Player player = parsePlayer(sCurrentLine);

                    if (player == null) {
                        return null;
                    }

                    if (flag == 0) {
                        tmpPlayer = player;
                    } else if (flag == 1) {
                        if (tmpPlayer.isWhite() == player.isWhite()) {
                            return null;
                        }
                        game = new Game(tmpPlayer, player);
                    }

                    flag += 1;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        assert game != null;
        if (game.getPlayer1() instanceof NetworkPlayer
                || game.getPlayer2() instanceof NetworkPlayer) {
            game.setFinished(true);
        }

        game.setDirty(false);
        return game;
    }

    protected Game parseLog(Game game, String input) {
        if (game == null) {
            return null;
        }

        game.reset();
        String[] lines = input.split("\n");

        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            Round round = Round.parseRound(line, game);
            StepStatus s = game.addRound(round);

            if (s != StepStatus.OK) {
                return null;
            }
        }

        return game;
    }

    protected String parsePlayer(Player player) {
        if (player == null) {
            return "null";
        }

        return player.getClass().getSimpleName() + " "
                + (player.isWhite() ? "white" : "black");
    }


    protected Player parsePlayer(String id) {
        if (id == null) {
            return null;
        }

        String[] parts = id.split(" ");

        if (parts.length == 2) {
            String cl = parts[0];
            boolean white = parts[1].equals("white");

            if (cl.equals(HumanPlayer.class.getSimpleName())) {
                return new HumanPlayer(white);
            } else if (cl.equals(ComputerPlayer.class.getSimpleName())) {
                return new ComputerPlayer(white);
            } else if (cl.equals(NetworkPlayer.class.getSimpleName())) {
                return new NetworkPlayer(white);
            }
        }

        return null;
    }

    protected String parseRounds(List<Round> rounds) {
        StringBuilder result = new StringBuilder();
        if (rounds == null) {
            return result.toString();
        }

        for (Round round : rounds) {
            result.append(round.toString()).append("\n");
        }

        return result.toString();
    }

    @Override
    public boolean save(File file, Game game) {
        BufferedWriter bw = null;

        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);

            bw.write(parsePlayer(game.getPlayer1()) + "\n");
            bw.write(parsePlayer(game.getPlayer2()) + "\n");

            List<Round> rounds = game.getRounds();

            bw.write(parseRounds(rounds));

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        return true;
    }

    public Game setLog(Game game, String input) {
        if (parseLog(new Game(game), input) == null) {
            return null;
        }

        return parseLog(game, input);
    }
}
