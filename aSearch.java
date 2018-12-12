//******************************************************************************
// -----------------------------------------------------------
// aSearch.java		                 				  
// -----------------------------------------------------------
//   Main Applet class for sample search applet
// -----------------------------------------------------------
// Author : R. BERTHOU
// E-Mail : rbl@berthou.com
// URL    : http://www.javaside.com  and http://www.berthou.com
// -----------------------------------------------------------
// Ver  * Author     *  DATE    * Description
// ....................DD/MM/YY...............................
// 1.00 * R.BERTHOU  * 28/02/00 * Creation
//******************************************************************************

import java.awt.* ;
import java.awt.event.ItemListener ;
import java.awt.event.ItemEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.ActionEvent ;
import java.awt.event.MouseListener ;
import java.awt.event.MouseEvent ;
import java.applet.Applet;

import java.io.BufferedReader ;
import java.io.InputStreamReader ;

import java.util.Hashtable ;
import java.util.Enumeration ;
import java.net.URL ;


public class aSearch extends Applet
    implements ItemListener, ActionListener, MouseListener
{

	public Hashtable dT    = new Hashtable (15);
	public Hashtable dList = new Hashtable (150);

	int		iMax = 1 ;

	private char        cSep = ';'   ;
	private String oldGrp = "" ;
	private String sTarg = "_new" ;

    Choice    cGroupe ;
    Choice    cEngine ;
    Button    bOk ;
	TextField tMots ;
	
	int iCol1 = 0x000000 ;

    public void init() {
		cGroupe = new Choice() ;
		cEngine = new Choice() ;
		bOk     = new Button("Go...") ;

        cGroupe.addItemListener(this) ;

		String sP = getParameter("file") ;
		if (sP == null)
		    sP = "./engines.txt" ;

        readFile( sP ) ;
		
		sP = getParameter("bgcolor") ;
		if (sP != null)
		    iCol1 = Integer.parseInt( sP );

		sP = getParameter("target") ;
		if (sP != null)
		    sTarg = new String (sP) ;

		tMots = new TextField("keyWords", 15) ;

        setLayout(null);

		add(cGroupe) ;
		setComponent(cGroupe, 5, 5, 80, 28, "list1") ;

		add(cEngine) ;
		setComponent(cEngine, 90, 5, 150, 28, "list2") ;

		add(tMots) ;
		setComponent(tMots, 5, 35, 170, 28, "text") ;

		add(bOk) ;
		bOk.addActionListener(this) ;
		setComponent(bOk, 195, 35, 45, 28, "button") ;

        initEng("-") ;

        addMouseListener(this) ;
        
	}
	
	void setComponent(Component c, int x, int y, int w, int h, String s) {
		c.setBounds(x, y, w, h) ;

		String sP = getParameter(s + "_cF") ;
		int iCol = (sP==null) ? 0x2020E0 : Integer.parseInt( sP );
		c.setForeground(new Color(iCol)) ;

		sP = getParameter(s + "_cB") ;
		iCol = (sP==null) ? 0xE0E077 : Integer.parseInt( sP );
		c.setBackground(new Color(iCol)) ;
	}

    public void initEng(String s) {
        cEngine.setVisible(false) ;
        cEngine.removeAll() ;
        rsch es;
        Enumeration e = dList.elements();
        while ( e.hasMoreElements() )
        {
            es = ( rsch )e.nextElement();
            if ( es.sGrp.equals(s) || s.equals("-") ) 
                cEngine.addItem(es.sName) ;
        }
        oldGrp = s ;
        cEngine.setVisible(true) ;
    }
    
	public void readFile(String f) {
	  dList.clear()	;

	  iMax = 0;

	  // open stream to a file which name is expressed relative to the document URL
	  BufferedReader fis = null ;

	  try {
		    fis = new BufferedReader( new InputStreamReader( (new URL( getCodeBase(), f )).openStream()) ) ;
	  } catch( Exception e ) {
	 	fis = null ;
      }	

	  rsch s = null ;

	  String sS ;

	  // parser loop
	  while ( true ) {
			s = new rsch();
 
			try {
				sS = fis.readLine() ;
			} catch( Exception e ) {
					break ;
			}	

			if (s.get( sS, cSep )) {
				if (s.sURL != null ) {
 					if (s.sName != null) {
                        dList.put( s.sName, s ) ;
                        if (dT.get(s.sGrp) == null) {
                            dT.put(s.sGrp, s.sGrp) ;
                            cGroupe.addItem(s.sGrp) ;
                        }
					    iMax++ ;
					}
				}
			}
			else 
				break ;
	  }

  }

  public void paint (Graphics g) {
        g.setColor(new Color(iCol1));
        
        g.fillRect(0,0,getSize().width, getSize().height) ;

        g.setColor(new Color(0 | ~iCol1));
        g.drawString("http://javaside.com", 5, getSize().height-3) ;

  } // end of paint
  
  public void itemStateChanged(ItemEvent e) {
        String s = cGroupe.getSelectedItem() ;
        
        if ( !s.equals(oldGrp) ) {
            initEng(s) ;
        }
  }
  
  public void actionPerformed(ActionEvent e)  {
        String s = cEngine.getSelectedItem() ;

        if (s.length() > 0) {
            s = ((rsch)dList.get(s)).sURL ;
            s = s + tMots.getText() ;

 			try{
    		    getAppletContext().showDocument(new URL(s), sTarg);
			}catch(Exception e2) { }
        }
  }

  public void mouseClicked(MouseEvent e)  { 
        int x = e.getX() ;
        int y = e.getY() ;
        
        // not very nice but work and fast
        if ((x > 5) && (x < 135) &&
            (y > (getSize().height - 20) ))
            try {
            	getAppletContext().showDocument(new URL("http://www.javaside.com"), "_blank");
            }
            catch (Exception ex) {}
    
  }
  
  public void mouseReleased(MouseEvent e) { }
  public void mousePressed(MouseEvent e)  { }
  public void mouseEntered(MouseEvent e)  { }
  public void mouseExited(MouseEvent e)   { }

  
}


class rsch {

	public String	sURL	= null ;	// dest URL
	public String	sName   = null   ;	// Name
	public String	sGrp    = null   ;	// Groupe

	public boolean get( String st, char cSep ) {
		int i = 0 ;
		int j = 0 ;
		int k = 0 ;

		String p ;

		if (st == null) return false ;

		while (true) {
			i = st.indexOf(cSep, j) ;
			if (i > 0)
				p = new String(st.substring(j, i).trim()) ;
			else
				p = new String(st.substring(j).trim()) ;


			if ((i>-1) || (p.length() > 0)) {
				if ( k == 0) sName   = new String(p) ;
				if ( k == 1) sGrp    = new String(p) ;
				if ( k == 2) sURL    = new String(p) ;
	
				k++ ;
			}

			if (i == -1)  break ;
			else j = i + 1 ;
		}

		return ( k > 0 ) ;
	}

}
