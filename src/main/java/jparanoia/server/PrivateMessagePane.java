package jparanoia.server;
import java.awt.Color;
import static java.awt.Color.black;
import static java.awt.Color.white;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.lang.invoke.MethodHandles;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public class PrivateMessagePane extends JPanel {
    private final static Logger logger = getLogger( MethodHandles.lookup().lookupClass());

    static SimpleAttributeSet pmAttributes = new SimpleAttributeSet();
    final ServerPlayer player;
    final Dimension IDEAL_DIMENSION = new Dimension( 250, 175 );
    JTextPane displayArea;
    JScrollPane scrollPane;
    DefaultStyledDocument privateMessageDocument;
    JTextField inputLine;
    StatusPanel statusPanel;

    public PrivateMessagePane( ServerPlayer paramServerPlayer ) {
        this.player = paramServerPlayer;
        pmAttributes.addAttribute( StyleConstants.FontConstants.Family, "SansSerif" );
        pmAttributes.addAttribute( StyleConstants.CharacterConstants.Foreground, Color.white );
        pmAttributes.addAttribute( StyleConstants.CharacterConstants.Size, 10 );
        this.displayArea = new JTextPane();
        this.displayArea.setEditable( false );
        this.displayArea.setEnabled( true );
        this.displayArea.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent paramAnonymousFocusEvent ) {
                if ( !JPServer.optionsMenu.quickCharsheetMenuItem.isSelected() ||
                        JPServer.charsheetPanel.playerComboBox.getSelectedItem() == PrivateMessagePane.this.player ) {
//                    if ( !JPServer.jvm140 || JPServer.quickNamesToggle ) {
//                        String str = JPServer.inputLine.getText();
//                        int i = JPServer.inputLine.getSelectionStart();
//                        if ( str.equals( "" ) || i == 0 || Character.isWhitespace( str.charAt( i - 1 ) ) ) {
//                            JPServer.inputLine.replaceSelection( PrivateMessagePane.this.player.getName() );
//                        } else {
//                            JPServer.inputLine.replaceSelection( " " + PrivateMessagePane.this.player.getName() );
//                        }
//                        str = JPServer.inputLine.getText();
//                        i = JPServer.inputLine.getCaretPosition();
//                        if ( i < str.length() && !Character.isWhitespace( str.charAt( i ) ) ) {
//                            JPServer.inputLine.replaceSelection( " " );
//                        }
//                        JPServer.quickNamesToggle = false;
//                    } else {
                        JPServer.quickNamesToggle = true;
//                    }
                } else {
                    JPServer.charsheetPanel.playerComboBox.setSelectedItem( PrivateMessagePane.this.player );
                    JPServer.quickNamesToggle = false;
                }
                JPServer.inputLine.requestFocus();
            }
        } );
        switch ( JPServer.currentColorScheme ) {
            case "White on Black":
                this.displayArea.setDisabledTextColor( white );
                this.displayArea.setBackground( black );
                break;
            case "Black on White":
                this.displayArea.setDisabledTextColor( black );
                this.displayArea.setBackground( white );
                break;
            default:
                logger.info( "PivateMessagePane error: no recognized color scheme selected..." );
                break;
        }
        this.scrollPane = new JScrollPane( this.displayArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        this.privateMessageDocument = new DefaultStyledDocument();
        this.inputLine = new JTextField( 20 );
        this.inputLine.setEnabled( false );
        this.inputLine.addActionListener( paramAnonymousActionEvent -> {
            String str = PrivateMessagePane.this.inputLine.getText();
            if ( !str.equals( "" ) ) {
                PrivateMessagePane.this.player.specificSend( "200" +
                        PrivateMessagePane.this.player.getID() +
                        JPServer.myPlayer.getID() +
                        str );
                PrivateMessagePane.this.addMyMessage( str );
                PrivateMessagePane.this.inputLine.setText( "" );
            }
            JPServer.inputLine.requestFocus();
            JPServer.sendCommand( "211" );
        } );
        this.inputLine.addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent paramAnonymousFocusEvent ) {
                JPServer.sendingPrivateMessage( PrivateMessagePane.this.player );
                PrivateMessagePane.this.player.statusPanel.statusNewMessage( false );
                JPServer.setSendMainText( false );
            }

            public void focusLost( FocusEvent paramAnonymousFocusEvent ) {
                JPServer.sendCommand( "211" );
            }
        } );
        this.statusPanel = new StatusPanel( this.player );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        this.displayArea.setMinimumSize( new Dimension( 200, 60 ) );
        this.displayArea.setPreferredSize( new Dimension( 200, 60 ) );
        this.displayArea.setMaximumSize( new Dimension( 200, 60 ) );
        this.scrollPane.setMinimumSize( new Dimension( 240, 60 ) );
        this.scrollPane.setPreferredSize( new Dimension( 240, 60 ) );
        this.inputLine.setMaximumSize( new Dimension( 240, 10 ) );
        this.inputLine.setPreferredSize( new Dimension( 240, 19 ) );
        add( this.scrollPane );
        add( this.inputLine );
        try {
            this.privateMessageDocument.insertString( this.privateMessageDocument.getLength(), this.player.getName() +
                    "\n", pmAttributes );
        } catch ( BadLocationException localBadLocationException ) {
            logger.info( "bad location exception" );
        }
        this.displayArea.setDocument( this.privateMessageDocument );
        this.player.setPMPane( this );
        PMAndStatusPanel localPMAndStatusPanel = new PMAndStatusPanel( this, this.statusPanel );
    }

    public void addMyMessage( String paramString ) {
        try {
            pmAttributes.addAttribute( StyleConstants.CharacterConstants.Foreground, JPServer.myPlayer.getChatColor() );
            this.privateMessageDocument.insertString( this.privateMessageDocument.getLength(), "  " +
                    JPServer.myPlayer.getName().substring( 0, 1 ) +
                    ": ", pmAttributes );
            pmAttributes.addAttribute( StyleConstants.CharacterConstants.Foreground, JPServer.textColor );
            this.privateMessageDocument.insertString( this.privateMessageDocument.getLength(), paramString +
                    "\n", pmAttributes );
            this.displayArea.setDocument( this.privateMessageDocument );
            if ( JPServer.autoScroll ) {
                this.displayArea.setCaretPosition( this.privateMessageDocument.getLength() );
            }
            if ( JPServer.keepLog ) {
                String str;
                if ( JPServer.htmlLog ) {
                    str = "<span class=\"pm\"><span class=\"player0\">(" +
                            JPServer.myPlayer.getName() +
                            "</span> -> <span class=\"player" +
                            this.player.getPlayerNumber() +
                            "\">" +
                            this.player.toString() +
                            ")</span>: " +
                            paramString +
                            "</span><br/>";
                } else {
                    str = "(" + JPServer.myPlayer.getName() + " -> " + this.player.toString() + "): " + paramString;
                }
                JPServer.log.logEntry( str );
            }
        } catch ( BadLocationException localBadLocationException ) {
            System.err.println( "Unhandled exception. (Bad Location)" );
        }
    }

    public void reflectNameChange() {
        try {
            pmAttributes.addAttribute( StyleConstants.CharacterConstants.Foreground, JPServer.myPlayer.getChatColor() );
            this.privateMessageDocument.insertString( this.privateMessageDocument.getLength(), "\n\nNew Clone Family:\n", pmAttributes );
            pmAttributes.addAttribute( StyleConstants.CharacterConstants.Foreground, JPServer.textColor );
            this.privateMessageDocument.insertString( this.privateMessageDocument.getLength(), this.player.getName() +
                    "\n", pmAttributes );
            this.displayArea.setDocument( this.privateMessageDocument );
            if ( JPServer.autoScroll ) {
                this.displayArea.setCaretPosition( this.privateMessageDocument.getLength() );
            }
        } catch ( BadLocationException localBadLocationException ) {
            System.err.println( "Unhandled exception. (Bad Location)" );
        }
    }

    public void enableInput() {
        this.inputLine.setEnabled( true );
    }

    public void disableInput() {
        this.inputLine.setEnabled( false );
    }

    public String toString() {
        return this.player.getName() + "'s private message pane";
    }
}


/* Location:              C:\Users\noahc\Desktop\JParanoia(1.31.1)\JParanoia(1.31.1).jar!\jparanoia\server\PrivateMessagePane.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */
