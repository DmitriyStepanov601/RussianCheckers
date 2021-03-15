package log;

import game.Game;
import java.io.File;

public interface IFileLog {
    Game open(File file);

    boolean save(File file, Game game);
}
