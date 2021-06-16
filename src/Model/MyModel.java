package Model;

import Client.Client;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import Client.IClientStrategy;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private int playerRow;
    private int playerCol;
    private Solution solution;
    private Server generateServer;
    private Server solverServer;
    private static int mazeNum=0;

    public MyModel() throws IOException {
        if(generateServer==null)
        {
            generateServer = new Server(5400,1000, new ServerStrategyGenerateMaze());
            generateServer.start();
        }
        if(solverServer==null)
        {
            solverServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
            solverServer.start();
        }

    }

    @Override
    public void generateMaze(int rows, int cols) throws UnknownHostException {
        Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
            @Override
            public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    toServer.flush();
                    int[] mazeDimensions = new int[]{rows, cols};
                    toServer.writeObject(mazeDimensions); //send maze dimensions to server
                    toServer.flush();
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                    byte[] decompressedMaze = new byte[rows*cols+10]; //allocating byte[] for the decompressed maze -
                    is.read(decompressedMaze); //Fill decompressedMaze with bytes
                    maze = new Maze(decompressedMaze);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        client.communicateWithServer();
        setChanged();
        notifyObservers("maze generated");
        // start position:
        movePlayer(0, 0);
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void updatePlayerLocation(MovementDirection direction) {
        switch (direction) {
            case UP:
                if (playerRow > 0 && maze.getMatrix()[playerRow - 1][playerCol] == 0)
                    movePlayer(playerRow - 1, playerCol);
                break;
            case DOWN:
                if (playerRow < maze.getMatrix().length - 1 && maze.getMatrix()[playerRow + 1][playerCol] == 0)
                    movePlayer(playerRow + 1, playerCol);
                break;
            case LEFT:
                if (playerCol > 0 && maze.getMatrix()[playerRow][playerCol - 1] == 0)
                    movePlayer(playerRow, playerCol - 1);
                break;
            case RIGHT:
                if (playerCol < maze.getMatrix()[0].length - 1 && maze.getMatrix()[playerRow][playerCol+1] == 0)
                    movePlayer(playerRow, playerCol + 1);
                break;
            case UPRIGHT:
                if (playerCol < maze.getMatrix()[0].length - 1 && playerRow > 0 && maze.getMatrix()[playerRow - 1][playerCol + 1] == 0)
                    movePlayer(playerRow - 1,playerCol + 1);
                break;
            case UPLEFT:
                if(playerCol > 0 && playerRow > 0 && maze.getMatrix()[playerRow - 1][playerCol - 1] == 0)
                    movePlayer(playerRow - 1,playerCol - 1);
                break;
            case DOWNLEFT:
                if(playerRow < maze.getMatrix().length - 1 && playerCol > 0 && maze.getMatrix()[playerRow + 1][playerCol - 1] == 0)
                    movePlayer(playerRow + 1,playerCol - 1);
                break;
            case DOWNRIGHT:
                if (playerRow < maze.getMatrix().length - 1 && playerCol < maze.getMatrix()[0].length - 1 && maze.getMatrix()[playerRow + 1][playerCol + 1] == 0)
                    movePlayer(playerRow + 1,playerCol + 1);
                break;
        }

    }

    private void movePlayer(int row, int col){
        this.playerRow = row;
        this.playerCol = col;
        if(row==maze.getRows()-1 && col==maze.getColumns()-1)
            playerWin();
        setChanged();
        notifyObservers("player moved");
    }

    @Override
    public int getPlayerRow() {
        return playerRow;
    }

    @Override
    public int getPlayerCol() {
        return playerCol;
    }

    @Override
    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    @Override
    public void solveMaze() throws UnknownHostException {
        //solve the maze
        if(maze!=null) {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (IClientStrategy) (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    toServer.flush();
                    toServer.writeObject(maze);
                    toServer.flush();
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    solution = (Solution) fromServer.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
            setChanged();
            notifyObservers("maze solved");
        }
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public void saveMaze() throws IOException {
        if (maze == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Your maze is not ready yet");
            alert.show();
        }
        else {
            while (true) {
                File c = new File("Resources/MazeFiles" + File.separator + mazeNum + "[" + maze.getRows() + "X" + maze.getColumns() + "]" + "Maze.maze");
                if (!c.exists())
                    break;
                mazeNum++;
            }
            String mazeFileName = mazeNum + "[" + maze.getRows() + "X" + maze.getColumns() + "]" + "Maze.maze";
            File newMazeFile = new File("Resources/MazeFiles" + File.separator + mazeFileName);
            newMazeFile.getParentFile().mkdirs();
            newMazeFile.createNewFile();
            OutputStream out = new MyCompressorOutputStream(new FileOutputStream("Resources/MazeFiles" + File.separator + mazeFileName));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
        }
    }

    @Override
    public void newMaze() throws UnknownHostException {
        generateMaze(25,25);
    }
    @Override
    public void exit() {
        generateServer.stop();
        solverServer.stop();
        Platform.exit();
    }

    @Override
    public void loadFile(File chosen) {
        int rows = Integer.parseInt(chosen.getName().substring(2, chosen.getName().indexOf('X')));
        int cols = Integer.parseInt(chosen.getName().substring(chosen.getName().indexOf('X') + 1, chosen.getName().indexOf(']')));

        Client client = null;
        try {
            client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream in, OutputStream outToServer) {
                    byte savedMazeBytes[] = new byte[0];
                    //read maze from file
                    in = null;
                    try {
                        in = new MyDecompressorInputStream(new FileInputStream(chosen));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    savedMazeBytes = new byte[rows * cols + 10];
                    try {
                        in.read(savedMazeBytes);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    maze = new Maze(savedMazeBytes);
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.communicateWithServer();
        setChanged();
        notifyObservers("maze generated");
        // start position:
        movePlayer(0, 0);
    }

    @Override
    public void drag(MouseEvent mouseEvent, double canvasHeight, double canvasWidth) {
        double cellHeight = canvasHeight / this.maze.getRows();
        double cellWidth = canvasWidth / this.maze.getColumns();
        System.out.println("col=" + playerCol);
        System.out.println("Row=" + playerRow);
        if(mouseEvent.getY()>(this.playerRow) * cellHeight+cellHeight/2 && mouseEvent.getX()<this.playerCol * cellWidth-cellWidth/2)
            updatePlayerLocation(MovementDirection.DOWNLEFT);
        else if(mouseEvent.getY()>(this.playerRow) * cellHeight+cellHeight/2 && mouseEvent.getX()>this.playerCol * cellWidth+cellWidth/2)
            updatePlayerLocation(MovementDirection.DOWNRIGHT);
        else if(mouseEvent.getY()<(this.playerRow) * cellHeight+cellHeight/2 && mouseEvent.getX()>this.playerCol * cellWidth+cellWidth/2)
            updatePlayerLocation(MovementDirection.UPRIGHT);
        else if(mouseEvent.getY()<(this.playerRow) * cellHeight+cellHeight/2 && mouseEvent.getX()<this.playerCol * cellWidth-cellWidth/2)
            updatePlayerLocation(MovementDirection.UPLEFT);

        if(mouseEvent.getY()>(this.playerRow) * cellHeight+cellHeight/2)
            updatePlayerLocation(MovementDirection.DOWN);
        else if(mouseEvent.getY()<(this.playerRow * cellHeight)-cellHeight/2)
            updatePlayerLocation(MovementDirection.UP);
        if(mouseEvent.getX()<this.playerCol * cellWidth-cellWidth/2)
            updatePlayerLocation(MovementDirection.LEFT);
        else if(mouseEvent.getX()>this.playerCol * cellWidth+cellWidth/2)
            updatePlayerLocation(MovementDirection.RIGHT);

    }

    @Override
    public void help() {

    }

    @Override
    public void scroll(ScrollEvent scrollEvent) {

    }
    private void playerWin() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("GOOD GAME");
        alert.setTitle("Congratulations!");
        alert.setContentText("yummm i like bones!");
        alert.show();
        Media victorySound = new Media(new File("Resources/sounds/victory.mp3").toURI().toString());
        MediaPlayer player = new MediaPlayer(victorySound);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.play();
    }
}
