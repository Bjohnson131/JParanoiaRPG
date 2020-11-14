package jparanoia.server;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class PMAndStatusPanel extends JPanel {
    public PMAndStatusPanel( PrivateMessagePane paramPrivateMessagePane, StatusPanel paramStatusPanel ) {
        setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
        paramStatusPanel.setPreferredSize( new Dimension( 35, 70 ) );
        paramStatusPanel.setMinimumSize( new Dimension( 35, 70 ) );
        if ( JPServer.serverOptions.isPXPGame() ) {
            SpinnerPanel localSpinnerPanel = new SpinnerPanel();
            add( localSpinnerPanel );
        }
        add( paramStatusPanel );
        add( paramPrivateMessagePane );
    }
}


/* Location:              C:\Users\noahc\Desktop\JParanoia(1.31.1)\JParanoia(1.31.1).jar!\jparanoia\server\PMAndStatusPanel.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */
