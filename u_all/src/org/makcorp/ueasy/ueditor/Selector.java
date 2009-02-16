/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */
package org.makcorp.ueasy.ueditor;

import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

import org.makcorp.ueasy.uflow.UServer;

// By now it is for documents only, but it is reusable for medias as well
public class Selector extends JDialog implements TreeSelectionListener, ActionListener {

	public static final int DOCUMENTS = 0;
	public static final int MEDIAS = 1;

	//	private UServer server;
	private String id;
	private String document;
	private int type;

	private Boolean selected = Boolean.FALSE;
	private String selection = null;

	private JTree tree;
	private JFrame frame = new JFrame();
	private JLabel label = new JLabel("init");
	private JButton ok;
	private JButton cancel;

	/** Constructor 
	 * @param type the type of files to be seen, either Selector.DOCUMENTS or Selector.MEDIAS
	 * medias not supported yet however
	 */
	public Selector (Frame parent, UServer server, String id, String document, int type) {
		super(parent,"titre",true);
	/*	//this.server = server;
		System.out.println("Opening Selector");
		this.id = id;
		this.document = document;
		this.type = type;

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new NodeInfo("root","/", true));
		visitDirectory (root);
		tree = new JTree (root);
		tree.setRootVisible(false);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new URenderer());
		ToolTipManager.sharedInstance().registerComponent(tree);

		// prevents multiple selection
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.getInputMap().put(KeyStroke.getKeyStroke("ctrl A"), "none");

		// build the GUI
		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		frame.setForeground(Color.black);
		frame.setBackground(Color.lightGray);
		frame.setSize(300,300);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		WindowCloser wc =  new WindowCloser();
		wc.setFlag(selected);
		frame.addWindowListener(wc);

		container.add(new JScrollPane((JTree)tree),"Center");
		label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		container.add(label, "South");

		ok = new JButton("OK");
		ok.addActionListener(this);

		cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		Container buttons = new Container();
		buttons.setLayout(new FlowLayout());
		buttons.add(ok);
		buttons.add(cancel);

		container.add(buttons,"North");
	*/	System.out.println("Selector opened");
		Container container = frame.getContentPane();
		container.add(new javax.swing.JLabel("hello"));
	}


	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == ok)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) return;
			NodeInfo nodeInfo = (NodeInfo)node.getUserObject();
			if (!nodeInfo.isDir())
				selected = Boolean.TRUE;
		}
		else if (ae.getSource() == cancel)
		{
			selected = Boolean.TRUE;
			selection = null;
		}
	}

	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null)
		{
			System.err.println("Null Node !");
			return;
		}
		NodeInfo nodeInfo = (NodeInfo)node.getUserObject();
		if (nodeInfo.isDir())
		{
			label.setText(" ");
			if (!nodeInfo.isVisited())
				visitDirectory (node);
		}
		else {
			if (!nodeInfo.getPath().equals(""))
			{
				selection = nodeInfo.getPath();
				label.setText(selection);
			}
		}
	}

	private void visitDirectory (DefaultMutableTreeNode node)
	{
		NodeInfo nodeInfo = (NodeInfo)node.getUserObject();
		//UrList list = server.runList(id, doc, nodeInfo.getPath());

		//String dirs = list.getDirs();
		//String documents = list.getDocuments();
		
		label.setText("... Please Wait ...");
		label.setForeground(java.awt.Color.red);
		frame.update(frame.getGraphics());
		
		// TODO to remove, here just for debug purpose
		try {
			Thread.sleep(1000);
			frame.update(frame.getGraphics());
		}
		catch (InterruptedException e) { e.printStackTrace(); }

		String dirs = "dir2:voyage,dir3:mer";
		String documents = "dir1/doc1.sgml:mon planning,dir2/cv.xml:Mon C.V.";
		String medias = "bullet.png:image/png:Liste,dir1/anim.qt:video/quicktime:Ma video";

		// directories
		StringTokenizer stk = new StringTokenizer (dirs, ",", false);
		while (stk.hasMoreTokens())
		{
			String token = stk.nextToken();
			StringTokenizer stk2 = new StringTokenizer (token, ":", false);
			try {
				String path = stk2.nextToken();
				String label = stk2.nextToken();
				node.add(new DefaultMutableTreeNode(new NodeInfo(label, path, true)));
			}
			catch (NoSuchElementException e)
			{
				System.err.println ("Malformed Uflow LIST dirs");
				e.printStackTrace();
			}
		}

		// documents
		stk = new StringTokenizer (documents, ",", false);
		while (stk.hasMoreTokens())
		{
			String token = stk.nextToken();
			StringTokenizer stk2 = new StringTokenizer (token, ":", false);
			try {
				String path = stk2.nextToken();
				String label = stk2.nextToken();
				node.add(new DefaultMutableTreeNode(new NodeInfo(label, path, false)));
			}
			catch (NoSuchElementException e)
			{
				System.err.println ("Malformed Uflow LIST documents");
				e.printStackTrace();
			}
		}

		nodeInfo.setVisited (true);
		
		label.setText(" ");
		label.setForeground(java.awt.Color.black);
		
	}

	public String getSelection()
	{
		selected = Boolean.FALSE;
		frame.setVisible(true);
		while (!selected.booleanValue())
		{
			try {
				Thread.sleep(100);
				System.out.print(".");System.out.flush();
				//frame.repaint();
			}
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		frame.setVisible(false);
		return selection;
	}

	/*public static void main(String args[])
	{
		Selector selector = new Selector ("","",Selector.DOCUMENTS);
		while (true)
		{
			System.out.println(selector.getSelection());
		}
	}*/
}

class WindowCloser extends WindowAdapter
{
	private Boolean b;

	public void windowClosing(WindowEvent e)
	{
		b = Boolean.TRUE;
		//Window win = e.getWindow();
		//win.setVisible(false);
	}
	public void setFlag(Boolean b)
	{
		this.b = b;
	}
}


class NodeInfo
{
	private String label;
	private String path;
	private boolean isDir;
	private boolean visited;

	public NodeInfo(String label, String path, boolean isDir)
	{
		this.label = label;
		this.path = path;
		this.isDir = isDir;
		this.visited = false;
	}

	public void setVisited (boolean b)
	{
		visited = b;
	}

	public String getPath ()
	{
		return path;
	}

	public boolean isDir ()
	{
		return isDir;
	}

	public boolean isVisited ()
	{
		return visited;
	}

	public String toString ()
	{
		return label;
	}
}

class URenderer extends DefaultTreeCellRenderer {
	ImageIcon unvisitedDir;

	public URenderer() {
		unvisitedDir = new ImageIcon("unvisited-dir.png");
	}

	public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean sel,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(
				tree, value, sel,
				expanded, leaf, row,
				hasFocus);
		if (leaf && isUnvisitedDir(value)) {
			setIcon(unvisitedDir);
			setToolTipText("Unvisited Directory");
		} else {
			setToolTipText("Directory");
		}

		return this;
	}

	protected boolean isUnvisitedDir(Object value) {
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)value;
		NodeInfo nodeInfo =
			(NodeInfo)(node.getUserObject());
		return nodeInfo.isDir() && !nodeInfo.isVisited();
	}
}
