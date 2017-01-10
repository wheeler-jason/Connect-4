import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// The driver class
public class Connect4 extends Applet implements Runnable, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Static definitions that are used throughout the code.
	static public int RED = 0;
	static public int YELLOW = 1;
	static public int EMPTY = -1;
	static public int HUMAN = 0;
	static public int COMPUTER = 1;
	
	Image m_imgBoard;				// Board image
	MediaTracker m_objTracker = new MediaTracker( this );

	// Board object. Contains the board and board logic.
	Board m_objBoard = new Board();
	
	// The minimax class that finds the next move.
	MiniMax m_objMiniMax = new MiniMax();
	
	// Button object and thread.
	Button m_objButton = new Button("Start");
	Thread m_objThread;
	
	// Flag to search for the next move.
	boolean m_bGetNextMove = false;

	// Default the starting piece to RED.
	int m_nPiece = RED;
	
	// Initialize the applet.
	public void init() 
	{
		// Set the applet size.
        setSize(680,530);  
        
        // Add the button to the applet and add the action listener.
        add( m_objButton );
        m_objButton.addActionListener( this );
        
        try 
        {
        	// Load the blank image.
        	m_imgBoard = getImage( getCodeBase(), "Connect4Board.png");
    		// Add the image to the media tracker object.
        	m_objTracker.addImage( m_imgBoard, 100 );

        	// Wait for all image loading to complete.
        	m_objTracker.waitForAll();        	
        } 
        catch (Exception e) 
        {
        }

        // Create the thread object.
        m_objThread = new Thread( this );
        // Start the thread.
        m_objThread.start();
	}
	
	// Reads the board then updates the GUI to show new state
	public void paint(Graphics g)
	{
		// Draw the title image.
		g.drawImage( m_imgBoard, 20, 50, null );
		
		for( int nRow=0; nRow<6; nRow++ )
		{
			for( int nCol=0; nCol<7; nCol++ )
			{
				if( m_objBoard.GetBoardData()[nRow][nCol] == RED )
				{
					g.setColor( Color.RED );
					g.fillOval( 34 + nCol * 90, 56 + nRow * 80, 70, 70 );
				}
				if( m_objBoard.GetBoardData()[nRow][nCol] == YELLOW )
				{
					g.setColor( Color.YELLOW );
					g.fillOval( 34 + nCol * 90, 56 + nRow * 80, 70, 70 );
				}
			}
		}
	
	}	
	
	// This method will run when the start button is pushed
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// Just in case, set the flag to false.
		m_bGetNextMove = false;
		
		// Clear the board for a new game.
		m_objBoard.Clear();
		
		// Default starting color.
		m_nPiece = RED;
		
		repaint();
		
		// Set our flag that will enable the thread to
		// select moves.
		m_bGetNextMove = true;
		
		// Disable the button to prevent re-entry.
		m_objButton.setEnabled( false );
	}
	
	@Override
	public void run() 
	{
		// Need the try because when the applet quits
		// it will throw an InterruptedException.
		try
		{
			// Keep the thread loop going.
			while( true )
			{
				// Delay a second.
				Thread.sleep( 1000 );
				
				// Check to see if we are done.
				if( m_bGetNextMove &&
					( m_objBoard.IsCatsGame() ||
					m_objBoard.DidSideWin( RED ) ||
					m_objBoard.DidSideWin( YELLOW ) ) )
				{
					// Re-enable the button.
					m_objButton.setEnabled( true );
					// Set the flag to false.
					m_bGetNextMove = false;
				}
				
				// If the flag is true...
				else if( m_bGetNextMove )
				{
					// Create a new Position object (which contains row and column)
					Position pos = new Position();
					
					// Set the search piece to whatever side is now going to move.
					m_objMiniMax.SetSearchPiece( m_nPiece );
					
					// Get the move from the minimax logic.
					m_objMiniMax.GetMove( pos, m_objBoard.GetBoardData(), m_nPiece );
					
					// Place the piece in the board.
					m_objBoard.PlacePiece( pos.Row, pos.Col, m_nPiece );
					
					// Call repaint.
					repaint();

					// Change whose turn it is
					m_nPiece = ( m_nPiece == RED ? YELLOW : RED );
				}

			}
		}
		catch(Exception ex){}
	}	
}
