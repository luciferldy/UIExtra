import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;


@SuppressWarnings("serial")
public class UIFrame extends JFrame{
	
	public static final int BUTTON_HEIGHT = 20;
	public static final int INLINE_HEIGHT = 10;
	
	private JPanel panel;
	private JButton addbtn;
	private JButton delbtn;
	private int number = 0;
	private ArrayList<JTextField> edit_group;
	private int[][] focus_jump = new int[30][4];
	
	public UIFrame(){
		this.setTitle("UIExtra");
		this.setSize(400, 600);
		this.setLocation(50, 30);
		edit_group = new ArrayList<JTextField>();
		// initial the focus_jump
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 4; j++) {
				focus_jump[i][j] = -1;
			}
		}
		initPanel();
	}
	
	public void initPanel(){
		panel = new JPanel();
		addbtn = new JButton("添加");
		delbtn = new JButton("删除");
		
		panel.setLayout(null);
		panel.add(addbtn);
		panel.add(delbtn);
		
		addbtn.setBounds(0, 0, 80, 20);
		delbtn.setBounds(90, 0, 80, 20);
		// add input
		addbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				number++;
				JTextField input = new JTextField();
				input.setName(number+"");
				panel.add(input);
				input.setBounds(0, BUTTON_HEIGHT*number+INLINE_HEIGHT*(number+1), 80, BUTTON_HEIGHT);
				input.setDragEnabled(true);
				input.addMouseMotionListener(new CustomMouseAdapter(input, number));
				panel.validate();
				panel.repaint();
				edit_group.add(input);
			}
		});
		
		// del button
		delbtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// if number == 0, return nothing
				if (number==0)
					return;
				panel.remove(edit_group.get(--number));
				panel.repaint();
			}
		});
		
		this.add(panel);
	}
	
	// 自定义鼠标监听
	public class CustomMouseAdapter extends MouseAdapter{
		
		private JTextField input;
		@SuppressWarnings("unused")
		private int off_List; // record the offset in list
		private int off_Left = 0;
		private int off_Top = 0;
		
		public CustomMouseAdapter(JTextField input, int number) {
			// TODO Auto-generated constructor stub
			this.input = input;
			off_List = number;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mousePressed");
		}
		
		@Override
        public void mouseDragged(MouseEvent e) {
            Point point = input.getLocation();            
            input.setLocation(e.getPoint().x + point.x - off_Left, e.getPoint().y + point.y - off_Top);
            
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("mouseReleased");
		}
	}

	// 自定义键盘监听
	
	// 更新下一个跳转的对象
	public void updateNextObject(){
		
		Point op, np;
		int ox = 0, oy = 0;
		int offx = 0, offy = 0; // 两个控件之间的偏移
		int distanceno = 0;
		for (int offset = 0; offset < number; offset++) {
			
			op = ((JTextField)edit_group.get(offset)).getLocation();
			ox = op.x;
			oy = op.y;
			for (int i = 0; i < number; i++) {
				if (i == offset) 
					continue;
				np = ((JTextField)edit_group.get(i)).getLocation();
				offx = np.x - ox;
				offy = np.y - oy;
				switch (getPosition(offx, offy)) {
				case 0:
					judgeAndChange(0, ox, oy, offset, distanceno, i);
					break;
				case 1:
					judgeAndChange(1, ox, oy, offset, distanceno, i);
					break;
				case 2:
					judgeAndChange(2, ox, oy, offset, distanceno, i);
					break;
				case 3:
					judgeAndChange(3, ox, oy, offset, distanceno, i);
					break;
				default:
					break;
				}
			}
		}
		
	}
	
	// 获取位置
	public int getPosition(int offx, int offy){
		int absx = Math.abs(offx);
		int absy = Math.abs(offy);
		int position = 0;
		if (absx > absy) {
			if (offx > 0) 
				position = 3; // 右
			else 
				position = 0; // 左
		}else {
			if(offy > 0)
				position = 2;
			else 
				position = 1;
		}
		return position;
	}
	
	// position表示上下左右 ，左0， 上1， 下2， 右3; 
	public void judgeAndChange(int position, int ox, int oy, int offset, int distanceno, int i){
		if (focus_jump[offset][position] == -1) {
			focus_jump[offset][position] = i;
		}else {
			Point temp = ((JTextField)edit_group.get(focus_jump[offset][0])).getLocation();
			int temx = temp.x;
			int temy = temp.y;
			int distance = (temx-ox)*(temx-ox)+(temy-oy)*(temy-oy);
			if (distance > distanceno) {
				focus_jump[offset][position] = i;
			}
		}
	}
}
