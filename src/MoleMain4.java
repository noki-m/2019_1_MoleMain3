// MoleMain4.java		ランダム表示
//叩かれたときモグラが消えて次のモグラが出てくる仕様について，（スレッドのsleepがずっと一定であることにより，）
//モグラがひっこむギリギリに叩いた場合，次のモグラの出現時間が短くなっていました。叩くのが不可能な速さなのにミスとして採点されてしまうのはなのはつまらないと思い，
//叩いた後に，あらためて2000数えて次のモグラを出すように改良しました

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MoleMain4 extends JFrame implements Runnable{
	Thread thread; // スレッドを使うためのインスタンスを宣言
	ImageIcon ic[] = new ImageIcon[4];
	int imageID = 0; // 表示する画像の番号

	static final int N_DIV = 3; // 縦と横の分割数(定数)
	Card b[] = new Card[N_DIV * N_DIV];
	int tr=0,fl=0; // モグラが出現している穴の番号，ターン数
	int target;
	double ok=0,x=0; //当たり，はずれ
	Random rand; // 乱数を発生するクラス
	JTextField tf;
	JPanel p;
	int a;	//カウンタ
	Object n;

	public static void main(String[] args) {
		MoleMain4 w = new MoleMain4();
		w.setTitle("もぐら叩きっぽい");
		w.setSize(620, 680); //Window のサイズをセット
		w.setVisible(true); //表示する
	}

	public MoleMain4 () {
		ic[0] = new ImageIcon("mole0.png"); // モグラ無
		ic[1] = new ImageIcon("mole1.png"); // モグラ有
		ic[2] = new ImageIcon("mole2.png"); // モグラヒット
		ic[3] = new ImageIcon("mole3.png"); // 穴ヒット

		tf = new JTextField("もぐらをたたこう！"); // テキストフィールド生成
		tf.setFocusable(false); // テキストフィールドを手動で変更できなくする

		p = new JPanel(); // パネルを生成
		p.setLayout(new GridLayout(N_DIV, N_DIV));
		for (int i = 0; i < N_DIV * N_DIV; i++) {
			b[i] = new Card(i); // ボタンi生成

			p.add(b[i]); // 配置
		}
		add(tf, BorderLayout.NORTH); //　テキストフィールドを上に配置
		add(p, BorderLayout.CENTER); // パネルを下に配置

		rand = new Random(System.currentTimeMillis()); // 現在時刻で乱数を初期化する

		setTarget();
		thread = new Thread(this); // スレッドを生成
		thread.start(); // スレッドを開始　(run()が呼ばれる)
	}

	public void clearTarget() {
		b[target].setIcon(ic[0]); // モグラ無に変更
		b[target].setPressedIcon(ic[3]); // モグラ無に変更
		target = -1;
		}

	public void setTarget() {
		target = rand.nextInt(N_DIV * N_DIV);
		b[target].setIcon(ic[1]); // モグラ有に変更
		b[target].setPressedIcon(ic[2]); // モグラ有に変更
		tr+=1;
	}

	public class Card extends JButton implements ActionListener {	//ボタン表示関係
		public Card(int i) {
			setIcon(ic[0]);
			setPressedIcon(ic[3]); // ボタンを押しているときに表示する画像
			setBorderPainted(false); // 枠線無し
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent ae) {				//何かしらボタン押したとき
			if(getIcon()==ic[1]) {
				fl=1;
				ok+=1;
				System.out.println("当たり！");
				tf.setText(""+(int)tr+"/ あたりです！　 [スコア]当たり:"+(int)ok+" , はずれ:"+(int)x+"  正確さ:"+ok/tr+"");
				n=ae.getSource();
				b[(int) n].setIcon(ic[0]); // モグラ無に変更
				b[(int) n].setPressedIcon(ic[3]); // モグラ無に変更
				target = -1;

				setTarget();
			}
			else if(getIcon()==ic[0]) {
				x+=1;
				System.out.println("はずれ！");
				tf.setText(""+(int)tr+"/ はずれです！ 　[スコア]当たり:"+(int)ok+" , はずれ:"+(int)x+"  正確さ:"+ok/tr+"");
			}
		}
	}

	@Override
	public void run() {
		while (true) { // このアプリの実行中は無限ループ
			try {
				if(fl==1) {ch();}
				if(fl==0) {
					System.out.println("!****はじまり");	//ループがいつ始まっていつ終わってるか確認用の表示
					ch();
					for(a=0;a<1000;) {			//なぜか2000だと4秒くらいになるので1000
						Thread.sleep(1); // 1ミリ秒待つ
						a+=1;
						if (a%10==0) {ch();}	//毎回叩かれたか確認すると処理重くなるかなと思って100おきにした
						if(a==-1) {break;}		//もしa=-1が帰ってきた（叩いた判定）とき，このforを抜けてclearTarget，setTargetする
					}
					System.out.println("****おわり");
				}
			} catch (InterruptedException e) { }
			clearTarget();
			setTarget();
			if(tr%3==0) {setTarget();}
			else if(tr%3==1) {clearTarget();}
		}
	}

	private void ch() {			//モグラがヒットした（fl=1）のときは，以下の処理をする
		if(fl==1) {		//ヒットした場合，叩いて新しいモグラが出た瞬間から2000カウントする．カウント終了後
				try {	//a=-1にしているので，↑のrun()野中のfor(a)から抜け出し，clearTarget，setTargetする
					System.out.println("!!++++はじまり");
					Thread.sleep(2000);
					System.out.println("++++おわり");
					fl=0;
					a=-1;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
}
