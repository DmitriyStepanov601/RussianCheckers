package gui;

import basis.Desk;
import basis.Figure;
import basis.Position;
import game.Game;
import game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoard extends JPanel {
    protected int dim;
    protected Game game;
    protected int h, hA;
    protected final int hD = 30;
    protected boolean isInteractive;
    protected Position pos;
    protected int w, wA;
    protected final int wD = 30;

    public GameBoard(Game game) {
        setGame(game);
        this.isInteractive = true;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                click(e.getX(), e.getY());
            }
        });
    }

    public void click(int x, int y) {
        if (x < wD || x > (wA + wD) || y < hD || y > (hA + hD)) {
            return;
        }

        int xN = (int) (Math.ceil((double) (x - wD) / wA * dim)) - 1;
        int yN = (int) (Math.ceil((double) (y - hD) / hA * dim)) - 1;

        char c = Position.getImageCol(xN);
        int r = Position.getImageRow(yN);

        Position actualPos = this.game.getPositionAt(c, r);
        Player cp = this.game.getCurrentPlayer();

        if (cp == null || actualPos == null) {
            return;
        }

        if (pos == null) {
            Figure f = actualPos.getFigure();

            if (f != null && f.isWhite() == cp.isWhite()
                    && cp.isInteractive() && !this.game.getFinished()
                    && this.isInteractive) {
                pos = actualPos;
                pos.select();
            }
        } else {
            cp.doMove(pos, actualPos);
            pos.unSelect();
            pos = null;
        }

        this.repaint();
    }

    private void drawTable(Graphics g, int w, int h) {
        // table & figures
        for (char c = Desk.START_LETTER; c <= this.game.getEndLetter(); c++) {
            for (int r = 1; r <= this.game.getDeskDimension(); r++) {
                Position p = this.game.getPositionAt(c, r);
                p.draw(g, 30, 30, w, h);

                Figure f = p.getFigure();
                if (f != null) {
                    f.draw(g, 30, 30, w, h);
                }
            }
        }

        // labels
        g.setColor(Color.black);
        for (int x = 0; x < this.game.getDeskDimension(); x++) {
            g.drawString(String.valueOf(x + 1), 30 / 2, x * h + 30 + h / 2);
            g.drawString(Character.toString((char) (Desk.START_LETTER + x)), x
                    * w + 30 + w / 2, 30 / 2);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game == null) {
            return;
        }

        w = (getWidth() - wD * 2) / dim;
        h = (getHeight() - hD * 2) / dim;
        wA = dim * w;
        hA = dim * h;

        drawTable(g, w, h);
    }

    public void setGame(Game game) {
        this.game = game;

        if (this.game != null) {
            this.dim = game.getDeskDimension();
        }
    }

    public void setInteractivity(boolean mode) {
        this.isInteractive = mode;
        this.repaint();
    }
}
