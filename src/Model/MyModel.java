package Model;

import Client.Client;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import Client.IClientStrategy;

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

    public MyModel() throws IOException {
        generateServer = new Server(5400,1000, new ServerStrategyGenerateMaze());
        generateServer.start();
        solverServer = new Server(5401,1000, new ServerStrategySolveSearchProblem());
        solverServer.start();
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
        //generateServer.stop(); /**********/
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
        Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
            @Override
            public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    toServer.writeObject(maze);
                    toServer.flush();
                    solution = (Solution) fromServer.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            });
        client.communicateWithServer();
        setChanged();
        notifyObservers("maze solved");
    }

    @Override
    public Solution getSolution() {
        return solution;
    }
}
