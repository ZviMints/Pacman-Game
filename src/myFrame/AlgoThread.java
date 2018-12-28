package myFrame;

import java.util.List;

import javax.swing.JOptionPane;
import Algorithm.Algo;
import Algorithm.Coordinate;
import Algorithm.GameToMatrix;
import Algorithm.Maze;
import Coords.MyCoords;
import GUI.Menu;
import Geom.Point3D;
import Player.Player;
import Robot.Play;

public class AlgoThread extends Thread{
	private GamePanel g;
	private Play play;
	private Player player;
	private static final MyCoords coords = new MyCoords();

	public AlgoThread(GamePanel g)
	{
		this.g = g;
		this.play = g.getPlay();
		this.player = g.getPlayer();
	}
	public void run()
	{
		if(player == null)
		{
			player = GetStartPointToPlayer();
			g.setPlayer(player);
		}
		play.setInitLocation(player.getP().x(),player.getP().y());
		play.start();
		g.setGameMode(true);
		g.setAlgoMode(true);
		while(play.isRuning())
		{
			GameToMatrix mat = new GameToMatrix(player,g.getFruitsList(),g.getBoxsList(),g.getGhostsList(),g.getPacmansList(),g.getMap());
			Maze maze = new Maze(mat.getMat());
			Algo algo = new Algo();
			List<Coordinate> path = algo.SOLVE(maze);
			if(path != null)
				path.remove(path.size() -1 ); // Its Back-tracking
			while(!path.isEmpty())
			{
				try { Thread.sleep(20);} // The animation wont run too fast				 
				catch (InterruptedException e) {} 
				Coordinate Next_Move_In_Pixels = path.remove(path.size()-1);
				Point3D dist = g.getMap().getCordFromPixel(new Point3D(Next_Move_In_Pixels));
				Point3D src = player.getP();
				double ang = coords.azimuth(src.x(), src.y(), dist.x(), dist.y());
				player.ang = ang;							
				//						System.out.println(map.getPixelFromCord(player.getP()));
				//						System.out.println(new Point3D(Next_Move_In_Pixels));
				//						System.out.println(ang);
				//						System.out.println();
				g.update();	
			}
			maze.reset();
		}
		g.update();	
		JOptionPane.showMessageDialog(null, "Algo Finished");
		Menu.SetVisableTrue();
		g.setGameMode(false);
		g.setAlgoMode(false);
	}
//	/* * * * * * * * * * * * * * * * * * getAngle * * * * * * * * * * * * * * * */
//	private double getAngle(double dx, double dy) {
//		if( dx == 0 && dy == 1) // {0,1}
//			return 180;
//		else if( dx == 1 && dy == 1) // {1,1}
//			return  135;
//		else if( dx == 1 && dy == 0) // {1,0}
//			return 90;
//		else if(dx == 1 && dy == -1 ) // {1,-1}
//			return 45;
//		else if(dx == 0 && dy == -1 ) //  {0,-1}
//			return 360;
//		else if( dx == -1 && dy == -1) // {-1,-1}
//			return 315;
//		else if( dx == -1 && dy == 0) // {-1,0}	
//			return 270;
//		else // {-1,1}
//			return 225;
//	}
	/* * * * * * * * * * * * * * * * * * GetStartPointToPlayer * * * * * * * * * * * * * * * */
	private Player GetStartPointToPlayer() 
	{
		if(!g.getPacmansList().isEmpty())
			return new Player(new Point3D(g.getPacmansList().get(0).getP().x(),g.getPacmansList().get(0).getP().y(),0),"Robot");
		else if(!g.getFruitsList().isEmpty())
			return new Player(new Point3D(g.getFruitsList().get(0).getP().x(),g.getFruitsList().get(0).getP().y(),0),"Robot");
		else
			return new Player(g.getMap().getP00(),"Robot");
	}
}
