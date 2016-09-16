package li.itzjakey.CrgSG.Utils;

import io.anw.Core.Main.SQL.DatabaseConnection;
import io.anw.Core.Main.SQL.DatabaseConnectionFactory;
import li.itzjakey.CrgSG.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager extends DatabaseConnection {

    public DatabaseManager(DatabaseConnectionFactory factory) {
        super(factory);
    }

    private static DatabaseManager instance = new DatabaseManager(
        DatabaseConnectionFactory.builder()
            .withHost(Main.getInstance().Config.getString("MySQL.Address"))
            .withPort(Main.getInstance().Config.getInt("MySQL.Port"))
            .withDatabase(Main.getInstance().Config.getString("MySQL.Database"))
            .withUsername(Main.getInstance().Config.getString("MySQL.Username"))
            .withPassword(Main.getInstance().Config.getString("MySQL.Password"))
    );

    public static DatabaseManager getInstance() {
        return instance;
    }

    public void checkDatabase() {
        try {
            this.getStatement().execute("CREATE TABLE IF NOT EXISTS sgData(username VARCHAR(255), kills INTEGER, deaths INTEGER, wins INTEGER, points INTEGER, gamesPlayed INTEGER, chestsOpened INTEGER)");
            LoggingUtils.log("Connected to database successfully!");
        } catch (SQLException | ClassNotFoundException e) {
            LoggingUtils.log(Level.SEVERE, "Database connection failed! Shutting down plugin...");
            Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
            e.printStackTrace();
        }
    }

    public boolean doesExist(UUID player) {
        ResultSet rs = null;
        try {
            rs = this.getStatement().executeQuery("SELECT COUNT(*) FROM sgData WHERE username='" + player.toString() + "';");
            rs.next();

            return rs.getInt(1) == 1;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void enterPlayer(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT COUNT(*) FROM sgData WHERE username='" + player.toString() + "';");
            rs.next();

            if(rs.getInt(1) == 0) {
                this.getStatement().execute("INSERT INTO sgData (username, kills, deaths, wins, points, gamesPlayed, chestsOpened) VALUES('" + player.toString() + "', 0, 0, 0, 0, 0, 0)");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getKills(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT kills FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addKill(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT kills FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET kills=" + (rs.getInt(1) + 1) + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setKills(UUID player, int kills) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT kills FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET kills=" + kills + " WHERE username='" + player.toString() + "';");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getDeaths(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT deaths FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addDeath(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT deaths FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET deaths=" + (rs.getInt(1) + 1) + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setDeaths(UUID player, int deaths) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT deaths FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET deaths=" + deaths + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getWins(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT wins FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addWin(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT wins FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET wins=" + (rs.getInt(1) + 1) + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setWins(UUID player, int wins) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT wins FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET wins=" + wins + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getGamesPlayed(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT gamesPlayed FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addGamePlayed(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT gamesPlayed FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET gamesPlayed=" + (rs.getInt(1) + 1) + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT points FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPoints(UUID player, int points) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT points FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET points=" + points + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setGamesPlayed(UUID player, int gamesPlayed) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT gamesPlayed FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET gamesPlayed=" + gamesPlayed + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getChestsOpened(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestsOpened FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addChestOpened(UUID player) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestsOpened FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET chestsOpened=" + (rs.getInt(1) + 1) + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setChestsOpened(UUID player, int chestsOpened) {
        try {
            ResultSet rs = this.getStatement().executeQuery("SELECT chestsOpened FROM sgData WHERE username='" + player.toString() + "';");

            if(rs.next()) {
                this.getStatement().execute("UPDATE sgData SET chestsOpened=" + chestsOpened + " WHERE username='" + player.toString() + "'");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
