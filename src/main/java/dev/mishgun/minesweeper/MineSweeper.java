package dev.mishgun.minesweeper;

public class MineSweeper 
{
    public static void main( String[] args )
    {
        MainLogic app = new MainLogic(30, 16);
        app.drawArea();
    }
}
