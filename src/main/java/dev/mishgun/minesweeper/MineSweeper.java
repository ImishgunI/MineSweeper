package dev.mishgun.minesweeper;

public class MineSweeper 
{
    public static void main( String[] args )
    {
        MainLogic app = new MainLogic(16, 30);
        app.drawArea();
    }
}
