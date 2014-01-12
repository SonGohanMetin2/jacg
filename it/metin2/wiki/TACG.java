/*
 * TACG: Theoretical Attack Calculus Gear
 * Copyright (C) 2013 Son Gohan (son.gohan.mt2@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.metin2.wiki;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import it.metin2.wiki.Weapon;
import static it.metin2.wiki.SkillCalculator.*;

public class TACG extends JFrame {

	/* JFrame constants */
	private final static String TITLE = "Theoretical Attack Calculus Gear by Son Gohan -- Alpha Release 1.0";
	// actually the window size is decided by 'tacg.pack()': changing these value may not influence the real size.
	private final static int DIM_X = 800;
	private final static int DIM_Y = 650;

	private JButton calcButton = new JButton("Calcola");
	/* PRELIMINARY (STATIC) GAME DEFINITIONS */
	private static String[] classes = { "Guerriero", "Sura", "Ninja", "Shamano" };
	private static String[][] jobs = {
					{ "Corporale", "Mentale" },
					{ "Magia Nera", "Armi Magiche" },
					{ "Corpo a Corpo", "Arciere" },
					{ "Guarigione", "Drago" }
				};
	
	private ClassPG selectedClass = ClassPG.WARRIOR;
	// PG STATS PANEL
	private JPanel statsPanel = new JPanel(new GridLayout(10,2));
	/// and its components
	private JComboBox<String> classPG = new JComboBox<>();
	private JComboBox<String> jobPG = new JComboBox<>();
	private ButtonGroup sex = new ButtonGroup();
	private JRadioButton	sexM = new JRadioButton("Maschio"),
				sexF = new JRadioButton("Femmina");
	private JLabel		aura = new JLabel("Livello Aura");
	private JLabel		estasi = new JLabel("Livello Estasi");
	private JTextField	pgStr = new JTextField(3),
				pgDex = new JTextField(3),
				pgInt = new JTextField(3),
				pgLiv = new JTextField(3),
				pgBonusVA = new JTextField(3);
	private JComboBox<String>	pgAura = new JComboBox<>(),
					pgEstasi = new JComboBox<>();
	// QUEST PANEL
	private JPanel questPanel = new JPanel(new GridLayout(6,1));
	/// and components
	private JCheckBox	libriMaledizione = new JCheckBox("Libri Maledizione"),
				palleDiGhiaccio = new JCheckBox("Palle di Ghiaccio"),
				tavoleTugyi = new JCheckBox("Tavole Tugyi"),
				notizieDeiCapi = new JCheckBox("Notizie dei Capi"),
				gioielliInvidia = new JCheckBox("Gioielli Invidia (atk)"),
				gioielliSaggezza = new JCheckBox("Gioielli Saggezza (atk)");
	// BONUS PANEL
	private JPanel bonusPanel = new JPanel(new GridLayout(7,4));
	/// and components
	private JTextField	bonusVsMobType = new JTextField("0",3),
				bonusVsMob = new JTextField("0",3),
				bonusVsPGClass = new JTextField("0",3),
				bonusVsPG = new JTextField("0",3),
				enemySpecificDefense = new JTextField("0",3),
				enemyInt = new JTextField("0",3),
				danniMedi = new JTextField("0",3),
				bonusAtkValue = new JTextField("0",3),
				enemyDefense = new JTextField("0",3);

	private JComboBox<String>	enemyFrenzyLv = new JComboBox<>(),
					enemyFearLv = new JComboBox<>(),
					enemyBlessingLv = new JComboBox<>();
	private JCheckBox		enemyDarkProtection = new JCheckBox();
	// WEAPON PANEL
	private JPanel weaponPanel = new JPanel(new GridBagLayout());
	/// and components
	private JComboBox<String> weaponsCB = new JComboBox<>();
	private JComboBox<Byte> upWeapon = new JComboBox<>();
	private ArrayList<Weapon> weapons = new ArrayList<>();
	private Weapon selectedWeapon;
	// RESULTS PANEL
	private JPanel results = new JPanel(new GridBagLayout());

	// and components
	private JTextField	resultsBaseAtk = new JTextField(15),
				resultsWeaAtk = new JTextField(15),
				resultsTheoAtk = new JTextField(15),
				resultsEffAtk = new JTextField(15),
				resultsEffAtkBonusVsPG = new JTextField(15),
				resultsEffAtkBonusVsMob = new JTextField(15),
				resultsDPSPiediVsPG = new JTextField(15),
				resultsDPSPiediVsMob = new JTextField(15),
				resultsDPSCavVsPG = new JTextField(15),
				resultsDPSCavVsMob = new JTextField(15),
				resultsTrafVsPG = new JTextField(15),
				resultsTrafVsMob = new JTextField(15);
	
	private UpdateListener updateListener = new UpdateListener();
	private FocusListener fl = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			//System.out.println("Move to end");
			JTextField field = ((JTextField)e.getComponent());
			field.selectAll();
			//field.setCaretPosition(field.getDocument().getLength());
		}
	};

	/** Constructor: fills itself with all components */
	public TACG() {

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,10,2,10);
		c.ipadx = 3;
		c.ipady = 3;

		/* Load weapons */
		loadWeapons();
	
		/* Attach actionlistener to class combo box */
		for(int i = 0; i < 4; ++i) 
			classPG.addItem(classes[i]);
		classPG.setSelectedItem("Guerriero");
		for(int i = 0; i < 2; ++i)
			jobPG.addItem(jobs[0][i]);
		jobPG.setSelectedItem("Corporale");
		selectedClass.setJob(jobs[0][0]);
		classPG.addActionListener(new ClassPGListener());
		jobPG.addActionListener(new JobPGListener());
		sexM.addActionListener(updateListener);
		sexF.addActionListener(updateListener);
		sex.add(sexM);
		sex.add(sexF);
		sexM.setSelected(true);
		JPanel sexPanel = new JPanel(new GridLayout(1,2));
		sexPanel.add(sexM);
		sexPanel.add(sexF);

		/* Create stats panel */
		statsPanel.setBorder(new CompoundBorder(new TitledBorder("Parametri PG"),new EmptyBorder(3,3,3,3)));
		statsPanel.add(new JLabel("Classe"));
		statsPanel.add(classPG);
		statsPanel.add(new JLabel("Specializz."));
		statsPanel.add(jobPG);
		statsPanel.add(new JLabel("Sesso"));
		statsPanel.add(sexPanel);
/*Liv*/		statsPanel.add(new JLabel("Livello"));
		pgLiv.setText("1");
		pgLiv.addActionListener(updateListener);
		pgLiv.addFocusListener(fl);
		statsPanel.add(pgLiv);
/*STR*/		statsPanel.add(new JLabel("STR"));
		pgStr.setText("6");
		pgStr.addActionListener(updateListener);
		pgStr.addFocusListener(fl);
		statsPanel.add(pgStr);
/*DEX*/		statsPanel.add(new JLabel("DEX"));
		pgDex.setText("3");
		pgDex.addActionListener(updateListener);
		pgDex.addFocusListener(fl);
		statsPanel.add(pgDex);
/*INT*/		statsPanel.add(new JLabel("INT"));
		pgInt.setText("3");
		pgInt.addActionListener(updateListener);
		pgInt.addFocusListener(fl);
		statsPanel.add(pgInt);
/*BonusVA*/	statsPanel.add(new JLabel("Bonus VA"));
		pgBonusVA.setText("0");
		pgBonusVA.addActionListener(updateListener);
		pgBonusVA.addFocusListener(fl);

		pgBonusVA.setToolTipText("Inserisci qui eventuali bonus sulla Velocità d'Attacco "+
					"dati da Equip o simili (non da abilità come Estasi!)");
		statsPanel.add(pgBonusVA);

/*aura/ecc*/	for(int i = 0; i < 41; ++i) {
			if(i < 20) {
				pgAura.addItem(""+i);
				pgEstasi.addItem(""+i);
				enemyFrenzyLv.addItem(""+i);
				enemyFearLv.addItem(""+i);
				enemyBlessingLv.addItem(""+i);
			} else if(i < 30) {
				pgAura.addItem("M"+(i-19));
				pgEstasi.addItem("M"+(i-19));
				enemyFrenzyLv.addItem("M"+(i-19));
				enemyFearLv.addItem("M"+(i-19));
				enemyBlessingLv.addItem("M"+(i-19));
			} else if(i < 40) {
				pgAura.addItem("G"+(i-29));
				pgEstasi.addItem("G"+(i-29));
				enemyFrenzyLv.addItem("G"+(i-29));
				enemyFearLv.addItem("G"+(i-29));
				enemyBlessingLv.addItem("G"+(i-29));
			} else {
				pgAura.addItem("P");
				pgEstasi.addItem("P");
				enemyFrenzyLv.addItem("P");
				enemyFearLv.addItem("P");
				enemyBlessingLv.addItem("P");
			}
		}
		pgAura.setSelectedItem("0");
		pgEstasi.setSelectedItem("0");
		enemyFrenzyLv.setSelectedItem("0");
		enemyFearLv.setSelectedItem("0");
		enemyBlessingLv.setSelectedItem("0");
		statsPanel.add(aura);
		pgAura.addActionListener(updateListener);
		statsPanel.add(pgAura);
/*estasi*/	statsPanel.add(estasi);
		pgEstasi.addActionListener(updateListener);
		statsPanel.add(pgEstasi);

		/* Create quest panel */
		questPanel.setBorder(new CompoundBorder(new TitledBorder("Missioni completate"),new EmptyBorder(3,3,3,3)));
		libriMaledizione.addActionListener(updateListener);
		questPanel.add(libriMaledizione);
		palleDiGhiaccio.addActionListener(updateListener);
		questPanel.add(palleDiGhiaccio);
		tavoleTugyi.addActionListener(updateListener);
		questPanel.add(tavoleTugyi);
		notizieDeiCapi.addActionListener(updateListener);
		questPanel.add(notizieDeiCapi);
		gioielliInvidia.addActionListener(updateListener);
		questPanel.add(gioielliInvidia);
		gioielliSaggezza.addActionListener(updateListener);
		questPanel.add(gioielliSaggezza);
		
		/* Create bonus panel */
		bonusPanel.setBorder(new CompoundBorder(new TitledBorder("Bonus"),new EmptyBorder(3,3,3,3)));
		bonusPanel.add(new JLabel("<html>% Forte vs &lt;<em>tipo di Mob</em>&gt;</html>"));
		bonusVsMobType.addActionListener(updateListener);
		bonusVsMobType.addFocusListener(fl);
		bonusPanel.add(bonusVsMobType);
		bonusPanel.add(new JLabel(" % Forte vs Mostri"));
		bonusVsMob.addActionListener(updateListener);
		bonusVsMob.addFocusListener(fl);
		bonusPanel.add(bonusVsMob);
		bonusPanel.add(new JLabel("<html>% Forte vs &lt;<em>classe PG</em>&gt;</html>"));
		bonusVsPGClass.addActionListener(updateListener);
		bonusVsPGClass.addFocusListener(fl);
		bonusPanel.add(bonusVsPGClass);
		bonusPanel.add(new JLabel(" % Forte vs Mezzi"));
		bonusVsPG.addActionListener(updateListener);
		bonusVsPG.addFocusListener(fl);
		bonusPanel.add(bonusVsPG);
		bonusPanel.add(new JLabel("% Dif. specifica nemica"));
		enemySpecificDefense.addActionListener(updateListener);
		enemySpecificDefense.addFocusListener(fl);
		bonusPanel.add(enemySpecificDefense);
		bonusPanel.add(new JLabel(" Lv Estasi nemica"));
		enemyFrenzyLv.addActionListener(updateListener);
		bonusPanel.add(enemyFrenzyLv);
		bonusPanel.add(new JLabel("Lv Paura nemica"));
		enemyFearLv.addActionListener(updateListener);
		bonusPanel.add(enemyFearLv);
		bonusPanel.add(new JLabel(" Lv Benedizione nemica"));
		enemyBlessingLv.addActionListener(updateListener);
		bonusPanel.add(enemyBlessingLv);
		bonusPanel.add(new JLabel("INT nemica"));
		enemyInt.addActionListener(updateListener);
		enemyInt.addFocusListener(fl);
		bonusPanel.add(enemyInt);
		bonusPanel.add(new JLabel(" % Danni Medi"));
		danniMedi.addActionListener(updateListener);
		danniMedi.addFocusListener(fl);
		bonusPanel.add(danniMedi);
		bonusPanel.add(new JLabel("Bonus valore atk"));
		bonusAtkValue.addActionListener(updateListener);
		bonusAtkValue.addFocusListener(fl);
		bonusPanel.add(bonusAtkValue);
		bonusPanel.add(new JLabel(" Difesa fisica nemica"));
		enemyDefense.addActionListener(updateListener);
		enemyDefense.addFocusListener(fl);
		bonusPanel.add(enemyDefense);
		bonusPanel.add(new JLabel("Protez. Oscura nemica"));
		enemyDarkProtection.addActionListener(updateListener);
		bonusPanel.add(enemyDarkProtection);

		/* Create weapon panel */
		weaponPanel.setBorder(new CompoundBorder(new EtchedBorder(),new EmptyBorder(2,2,2,2)));
		
/*Arma-label*/	JLabel lab = new JLabel("Arma");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.7;
		c.gridx = 0; 
		c.gridy = 0;
		weaponPanel.add(lab,c);
		
/*Up-label*/	lab = new JLabel("Up");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.25;
		c.gridx = 1;
		c.gridy = 0;
		weaponPanel.add(lab,c);
		
/*ElencoArmi*/	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		weaponsCB.addActionListener(new WeaponSelectionListener());
		weaponsCB.setPrototypeDisplayValue("Arco da Battaglia Cavalleresco (Liv 25)");
		weaponPanel.add(weaponsCB,c);
		weaponsCB.setSelectedItem(0);
		selectedWeapon = findWeapon((String)weaponsCB.getSelectedItem());
		
/*Up*/		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1; 
		c.gridy = 1;
		for(byte i = 0; i < 10; ++i) 
			upWeapon.addItem(i);
		upWeapon.setSelectedItem(0);
		upWeapon.addActionListener(updateListener);
		weaponPanel.add(upWeapon,c);

		/* Create results panel */
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.ipadx = 0;
		c.weightx = 0;
		c.gridwidth = 2;
/*AtkBase*/	results.add(new JLabel("Attacco base"),c);
		resultsBaseAtk.setToolTipText("L'attacco del tuo PG privo di armi e di bonus.");
		resultsBaseAtk.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsBaseAtk,c);
		
		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
/*AtkArma*/	results.add(new JLabel("Attacco arma"),c);
		resultsWeaAtk.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsWeaAtk,c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0;
/*AtkTeo*/	results.add(new JLabel("Atk finale teorico"),c);
		resultsTheoAtk.setToolTipText("L'attacco del tuo PG segnato sulla Finestra PG");
		resultsTheoAtk.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsTheoAtk,c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
/*AtkEff*/	results.add(new JLabel("Atk finale effettivo"),c);
		resultsEffAtk.setToolTipText("Stima del danno effettivo che effettueresti a un bersaglio con difesa ed elusione 0 (senza bonus)");
		resultsEffAtk.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsEffAtk,c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0;
/*AtkEffBvsPG*/	results.add(new JLabel("Atk finale effettivo con Bonus (vs PG)"),c);
		resultsEffAtkBonusVsPG.setToolTipText("Stima del danno effettivo che effettueresti a un PG con difesa e elusione 0 (con bonus)");
		resultsEffAtkBonusVsPG.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsEffAtkBonusVsPG,c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
/*AtkEffBonus*/	results.add(new JLabel("Atk finale effettivo con Bonus (vs mob)"),c);
		resultsEffAtkBonusVsMob.setToolTipText("Stima del danno effettivo che effettueresti a un mob con difesa ed elusione 0 (con bonus)");
		resultsEffAtkBonusVsMob.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsEffAtkBonusVsMob,c);

		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Media di danni/secondo a piedi (vs PG)"),c);
		resultsDPSPiediVsPG.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsDPSPiediVsPG,c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Media di danni/secondo a piedi (vs mob)"),c);
		resultsDPSPiediVsMob.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsDPSPiediVsMob,c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Media di danni/secondo a cavallo (vs PG)"),c);
		resultsDPSCavVsPG.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsDPSCavVsPG,c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Media di danni/secondo a cavallo (vs mob)"),c);
		resultsDPSCavVsMob.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsDPSCavVsMob,c);

		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Danno trafiggente vs PG"),c);
		resultsTrafVsPG.setEditable(false);
		c.gridx = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsTrafVsPG,c);

		c.gridx = 3;
		c.gridwidth = 2;
		c.weightx = 0;
		results.add(new JLabel("Danno trafiggente vs mob"),c);
		resultsTrafVsMob.setEditable(false);
		c.gridx = 5;
		c.gridwidth = 1;
		c.weightx = 1;
		results.add(resultsTrafVsMob,c);

		/* Fill JFrame */

		//c = new GridBagConstraints();
/*Classe*///	JPanel classPanel = new JPanel(new GridLayout(2,1));
//		classPanel.setBorder(new CompoundBorder(new TitledBorder("Classe PG"),new EmptyBorder(5,5,5,5)));
		//classPanel.add(new JLabel("Classe PG"));
//		classPanel.add(classPG);
//		classPanel.add(jobPG);
//		c.fill = GridBagConstraints.HORIZONTAL;
//		c.gridx = 0;
//		c.gridy = 0;
		//c.weightx = 0.3;
//		c.weightx = 0.3;
//		add(classPanel,c);
		
/*StatsPG*/	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.7;
		c.gridheight = 2;
		add(statsPanel,c);

/*Quests*/	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.3;
		c.weighty = 0.7;
		c.gridheight = 1;
		add(questPanel,c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.weightx = 0.3;
		c.weighty = 0.3;
		add(weaponPanel,c);

/*BonusPG*/	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		add(bonusPanel,c);
		
/*PanelArmi*/	/*c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; 
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		add(weaponPanel,c);*/

/*Risultati*/	c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		add(results,c);

/*CalcButton*/	c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.ipadx = 40;
		c.gridx = 0;
		c.weightx = 1;
		c.gridy = 4;
		calcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				calculate();
			}
		});
		add(calcButton,c);
	}

	/** Read the weapon database and fill weapon list accordingly. */
	private void loadWeapons() {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("it/metin2/wiki/weapondb.txt")))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				Weapon wea = Weapon.getFromLine(line);
				if(wea != null) {
					weapons.add(wea);
					weaponsCB.addItem(wea.getName());
				} 
			}
		} catch(IOException e) {
			System.err.println("Error: could not open weapon database. Your jar file is probably corrupt.");
		} catch(Exception e) {
			reportMsg(e);
		}
	}
	
	/** Find a weapon with given name in weapon list, and return it. */
	private Weapon findWeapon(String name) {
		for(Weapon w : weapons) {
			if(w.getName().equals(name)) return w;
		}
		return null;
	}

	public static void main(String[] args) {
		if(args.length > 0) {
			System.err.println("\n--- Java Attack Calculus Gear by Son Gohan ---");
			System.err.println("\nUsage: java -jar jacg.jar [takes no argument]\n");
			System.exit(0);
		}
		final TACG tacg = new TACG();
		tacg.setTitle(TITLE);
		tacg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Launching Java Attack Calculus Gear...");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tacg.setSize(DIM_X,DIM_Y);
				tacg.pack();
				tacg.setVisible(true);
			}
		});
	}

	class ClassPGListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String className = (String)((JComboBox)e.getSource()).getSelectedItem();

			/* Update jobs combo box */
			for(ActionListener al : jobPG.getActionListeners())
				jobPG.removeActionListener(al);
			jobPG.removeAllItems();
			for(int i = 0; i < 2; ++i)
				jobPG.addItem(jobs[Arrays.asList(classes).indexOf(className)][i]);

			/* update selectedClass and aura/estasi labels */
			if(className.equals("Guerriero")) {
				selectedClass = ClassPG.WARRIOR;
				aura.setText("Livello Aura");
				estasi.setText("Livello Estasi");
			} else if(className.equals("Sura")) {
				selectedClass = ClassPG.SURA;
				aura.setText("---");
				estasi.setText("---");
			} else if(className.equals("Ninja")) {
				selectedClass = ClassPG.NINJA;
				aura.setText("---");
				estasi.setText("---");
			} else if(className.equals("Shamano")) {
				selectedClass = ClassPG.MAGE;
				aura.setText("Livello Attacco+");
				estasi.setText("---");
			}
			selectedClass.setJob(jobs[Arrays.asList(classes).indexOf(className)][0]);
			jobPG.addActionListener(new JobPGListener());
			calculate();
		}
	}

	class JobPGListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String jobName = (String)((JComboBox)e.getSource()).getSelectedItem();
			selectedClass.setJob(jobName);

			if(jobName.equals("Corporale")) {
				aura.setText("Livello Aura");
				estasi.setText("Livello Estasi");
			} else if(jobName.equals("Armi Magiche")) {
				aura.setText("Livello Lama");
				estasi.setText("---");
			} else if(jobName.equals("Guarigione")) {
				aura.setText("Livello Attacco+");
				estasi.setText("---");
			} else {
				aura.setText("---");
				estasi.setText("---");
			}

			calculate();
		}
	}

	class UpdateListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			calculate();
		}
	}

	class WeaponSelectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			selectedWeapon = findWeapon((String)((JComboBox)e.getSource()).getSelectedItem());
			calculate();
		}
	}

	/* Calculate theoretical atk with 1st Mystikal-Gohan law */
	private int[] mgTheoAtk(boolean considerWeapon) throws IllegalArgumentException {
		int lv = Integer.parseInt(pgLiv.getText());
		int dex = Integer.parseInt(pgDex.getText());
		int str = Integer.parseInt(pgStr.getText());
		int stat = 0;
		switch(selectedClass.getStat()) {
			case STR:
				stat = str;
				break;
			case INT:
				stat = Integer.parseInt(pgInt.getText());
				break;
			case DEX:
				stat = dex;
				break;
		}
		//System.out.println("str="+str+", dex="+dex+", stat="+stat);
		if(considerWeapon) {
			int atkMin = selectedWeapon.getAtk((byte)upWeapon.getSelectedItem(),0,0);
			int atkMax = selectedWeapon.getAtk((byte)upWeapon.getSelectedItem(),0,1);
			return new int[] {
				(int)(2*lv+(1/75.)*(70+(int)(lv+2*dex)/9.)*(str+atkMin+(stat+atkMin)/2)),
				(int)(2*lv+(1/75.)*(70+(int)(lv+2*dex)/9.)*(str+atkMax+(stat+atkMax)/2))
			};
		} else {
			return new int[] {
				(int)(2*lv+(1/75.)*(70+(int)(lv+2*dex)/9.)*(str+stat/2)),
				(int)(2*lv+(1/75.)*(70+(int)(lv+2*dex)/9.)*(str+stat/2))
			};
		}
	}

	/* Calculate effective atk with 1st Mystikal-Gohan law */
	private int[] mgEffAtk() throws IllegalArgumentException {
		int lv = Integer.parseInt(pgLiv.getText());
		int dex = Integer.parseInt(pgDex.getText());
		int str = Integer.parseInt(pgStr.getText());
		int stat = 0;
		switch(selectedClass.getStat()) {
			case STR:
				stat = str;
				break;
			case INT:
				stat = Integer.parseInt(pgInt.getText());
				break;
			case DEX:
				stat = dex;
				break;
		}
		//System.out.println("str="+str+", dex="+dex+", stat="+stat);
		return new int[] {
			(int)(2*(lv+selectedWeapon.growth((byte)upWeapon.getSelectedItem()))+(1/75.)*(70+(int)(2*dex-0.6*lv)/9.)*(str+(3*selectedWeapon.getAtk(0,0,0)+stat)/2)),
			(int)(2*(lv+selectedWeapon.growth((byte)upWeapon.getSelectedItem()))+(1/75.)*(70+(int)(2*dex-0.6*lv)/9.)*(str+(3*selectedWeapon.getAtk(0,0,1)+stat)/2))
		};
	}
		
	private int powerups() throws IllegalArgumentException {
		int pow = 0;

		/* Ignore "pgAura" if the class is not one of those which actually have a powerup skill */
		if(	(pgAura != null) && (pgAura.getSelectedItem() != null) &&
			(selectedClass == ClassPG.WARRIOR && selectedClass.getJob().equals("Corporale") ||
			selectedClass == ClassPG.SURA && selectedClass.getJob().equals("Armi Magiche") ||
			selectedClass == ClassPG.MAGE && selectedClass.getJob().equals("Drago"))
		) {
			switch(selectedClass) {
				case WARRIOR:
					pow += auraDellaSpada((String)pgAura.getSelectedItem(),Integer.parseInt(pgLiv.getText()),Integer.parseInt(pgStr.getText()));
					break;
				case SURA:
					pow += lamaMagica((String)pgAura.getSelectedItem(),Integer.parseInt(pgLiv.getText()),Integer.parseInt(pgInt.getText()));
					break;
				case MAGE:
					pow += attaccoPiu((String)pgAura.getSelectedItem(),Integer.parseInt(pgInt.getText()));
					break;
			}
		}

		if(palleDiGhiaccio.isSelected()) pow += 50;
		if(gioielliInvidia.isSelected()) pow += 51;
		if(gioielliSaggezza.isSelected()) pow += 60;
		pow += Integer.parseInt(bonusAtkValue.getText());

		return pow;
	}

	/** @return { bonusI vs Mobs, bonusI vs PGs } */
	private float[] bonusI() throws IllegalArgumentException {
		return new float[] {	(float)((1 + (Integer.parseInt(bonusVsMobType.getText()))/100f) *
						(1 + (Integer.parseInt(bonusVsMob.getText()))/100f) *
						(1 + (tavoleTugyi.isSelected() ? 0.1f : 0f)))	//assuming this Chaegirab's bonus
												//is of Type I
					,
					(float)((1 + (Integer.parseInt(bonusVsPGClass.getText()))/100f) *
						(1 + (Integer.parseInt(bonusVsPG.getText()))/100f) *
						(1 + (notizieDeiCapi.isSelected() ? 0.08f : 0f)) *
						(1 + (tavoleTugyi.isSelected() ? 0.1f : 0f)))
				};
	}

	/** @return { bonusII vs Mobs, bonusI vs PGs } */
	private float[] bonusII() throws IllegalArgumentException {
		return new float[] {	1f	// no bonusII vs mobs: just buffs on enemy PG
					,
					(float)((1 + (-Integer.parseInt(enemySpecificDefense.getText()))/100f) *
						(1 + frenzy((String)enemyFrenzyLv.getSelectedItem())/100f) *
						(1 + (-fear((String)enemyFearLv.getSelectedItem()))/100f) *
						(1 + (-blessing((String)enemyBlessingLv.getSelectedItem(),Integer.parseInt(enemyInt.getText()))/100f)))
				};
	}

	/** @return { bonusIII vs Mobs, bonusI vs PGs } */
	private float[] bonusIII() throws IllegalArgumentException {
		return new float[] {	(float)(1 + (Integer.parseInt(danniMedi.getText()))/100f)
					, 
					(float)((1 + (Integer.parseInt(danniMedi.getText()))/100f) *
						(1 + (-(enemyDarkProtection.isSelected() ? 0.33f : 0))))
				};
	}

	/** @return { {damageVsMobMin,Max},{damageVsPGMin,Max}} */
	private int[][] effDamage(boolean piercing) throws IllegalArgumentException {
		return new int[][] {
		{
			(int)((Math.max(0,(mgEffAtk()[0]+powerups())*bonusI()[0]-Integer.parseInt(enemyDefense.getText()))*bonusII()[0]+(piercing ? Integer.parseInt(enemyDefense.getText()) : 0))*bonusIII()[0]),
			(int)((Math.max(0,(mgEffAtk()[1]+powerups())*bonusI()[0]-Integer.parseInt(enemyDefense.getText()))*bonusII()[0]+(piercing ? Integer.parseInt(enemyDefense.getText()) : 0))*bonusIII()[0])
		},
		{
			(int)((Math.max(0,(mgEffAtk()[0]+powerups())*bonusI()[1]-Integer.parseInt(enemyDefense.getText()))*bonusII()[1]+(piercing ? Integer.parseInt(enemyDefense.getText()) : 0))*bonusIII()[1]),
			(int)((Math.max(0,(mgEffAtk()[1]+powerups())*bonusI()[1]-Integer.parseInt(enemyDefense.getText()))*bonusII()[1]+(piercing ? Integer.parseInt(enemyDefense.getText()) : 0))*bonusIII()[1])
		}
		};
	}

	/** @return { DPS vs mob, DPS vs PG } -- or -1 in bug region */
	private float[] dpsPiedi() throws IllegalArgumentException {
		
		float hps = 0; //hits per second
		short va = (short)(100 + Integer.parseInt(pgBonusVA.getText()) + selectedWeapon.getVA((byte)upWeapon.getSelectedItem()));

		if(libriMaledizione.isSelected()) va += 5;
		if(tavoleTugyi.isSelected()) va+= 6;

		if(va > 165) return new float[] { -1f,-1f };

		switch(selectedWeapon.getType()) {
			case SPADONE:
				hps = 0.009f * va + 0.1f;
				break;
			case SPADA:
			case CAMPANA:
				hps = 0.014f * va;
				break;
			case VENTAGLIO:
				if(va < 141)
					hps = 0.014f * va;
				else return new float[] { -1f,-1f };
				break;
			case PUGNALE:
				hps = 0.021f * va;
				break;
			case ARCO:
				if(va > 150) return new float[] { -1f,-1f };
				hps = (sexM.isSelected() ? 0.006f : 0.009f) * va;
				break;
		}

		int[][] eD = effDamage(false);

		return new float[] { 
			(float)(hps * (eD[0][0] + eD[0][1])/2f),
			(float)(hps * (eD[1][0] + eD[1][1])/2f)
		};
	}

	/** @return { DPS vs mob, DPS vs PG } -- or -1 in bug region */
	private float[] dpsCav() throws IllegalArgumentException {
		
		float hps = 0; //hits per second
		short va = (short)(100 + Integer.parseInt(pgBonusVA.getText()) + selectedWeapon.getVA((byte)upWeapon.getSelectedItem()));

		if(libriMaledizione.isSelected()) va += 5;
		if(tavoleTugyi.isSelected()) va+= 6;

		if(va > 165) return new float[] { -1f,-1f };

		switch(selectedWeapon.getType()) {
			case SPADONE:
				hps = 0.0175f * va + 0.2f;
				break;
			case SPADA:
			case PUGNALE:
				hps = 0.018f * va;
				break;
			case VENTAGLIO:
			case ARCO:
				return new float[] { -1f,-1f };
			case CAMPANA:
				if(va < 145)
					hps = 0.0215f * va;
				else if(va > 155)
					hps = 0.0077f * va + 0.5f;
				else return new float[] { -1f,-1f };
				break;
		}

		int[][] eD = effDamage(false);

		return new float[] { 
			(float)(hps * (eD[0][0] + eD[0][1])/2f),
			(float)(hps * (eD[1][0] + eD[1][1])/2f)
		};
	}

	/** This is the method which updates all the result fields, recalculating them each time it's called. */
	public void calculate() {
		/* First, update weapon atk */
		byte up = (byte)upWeapon.getSelectedItem();
		try {
			if(up < 0 || up > 9) {
				setAllText("Up dell'arma invalido.");
				return;
			}
			resultsWeaAtk.setText(selectedWeapon.getAtk(up,0,0)+" - "+selectedWeapon.getAtk(up,0,1));
			/* Then, calculate base atk of character */
			resultsBaseAtk.setText(""+(mgTheoAtk(false)[0]+powerups()));
			/* Calculate theoretical and effective final atk with 1st and 2nd MG law */
			resultsTheoAtk.setText((mgTheoAtk(true)[0]+powerups())+" - "+(mgTheoAtk(true)[1]+powerups()));
			resultsEffAtk.setText((mgEffAtk()[0]+powerups())+" - "+(mgEffAtk()[1]+powerups()));
			/* Now add Bonus I,II and III to effective atk */
			int[][] efD = effDamage(false);
			resultsEffAtkBonusVsPG.setText(efD[1][0]+" - "+efD[1][1]);
			resultsEffAtkBonusVsMob.setText(efD[0][0]+" - "+efD[0][1]);
			/* Calculate Damage Per Second */
			float[] dpsP = dpsPiedi();
			if(dpsP[1] == -1f)
				resultsDPSPiediVsPG.setText("???");
			else
				resultsDPSPiediVsPG.setText(String.format("%.1f",dpsP[1]));
			if(dpsP[0] == -1f)
				resultsDPSPiediVsMob.setText("???");
			else
				resultsDPSPiediVsMob.setText(String.format("%.1f",dpsP[0]));
			float[] dpsC = dpsCav();
			if(dpsC[1] == -1f)
				resultsDPSCavVsPG.setText("???");
			else
				resultsDPSCavVsPG.setText(String.format("%.1f",dpsC[1]));
			if(dpsC[0] == -1f)
				resultsDPSCavVsMob.setText("???");
			else
				resultsDPSCavVsMob.setText(String.format("%.1f",dpsC[0]));
			/* Finally, calculate piercing damage */
			efD = effDamage(true);
			resultsTrafVsPG.setText(efD[1][0]+" - "+efD[1][1]);
			resultsTrafVsMob.setText(efD[0][0]+" - "+efD[0][1]);
		} catch(NumberFormatException e) {
			//System.err.println("Number format exception in calculate(): ");
			//e.printStackTrace();
			setAllText("Inserisci i parametri del PG");
			resultsWeaAtk.setText(selectedWeapon.getAtk(up,0,0)+" - "+selectedWeapon.getAtk(up,0,1));
		} catch(IllegalArgumentException e) {
			setAllText("Parametro/i invalido/i: "+e.getMessage());
		}
	}

	private void setAllText(String s) {
		resultsBaseAtk.setText(s);
		resultsWeaAtk.setText(s);
		resultsTheoAtk.setText(s);
		resultsEffAtk.setText(s);
		resultsEffAtkBonusVsPG.setText(s);
		resultsEffAtkBonusVsMob.setText(s);
		resultsDPSPiediVsPG.setText(s);
		resultsDPSPiediVsMob.setText(s);
		resultsDPSCavVsPG.setText(s);
		resultsDPSCavVsMob.setText(s);
		resultsTrafVsPG.setText(s);
		resultsTrafVsMob.setText(s);
	}

	public static enum Stat { VIT, STR, INT, DEX };
	
	enum ClassPG {

		WARRIOR("Guerriero",Stat.STR), 
		SURA("Sura",Stat.STR), 
		NINJA("Ninja",Stat.DEX),
		MAGE("Shamana",Stat.INT);

		private final Stat stat;
		private String name;
		private String job;

		ClassPG(String name,Stat stat) {
			this.name = name;
			this.stat = stat;
		}

		public Stat getStat() { return stat; }
		public String getName() { return name; }
		public String getJob() { return job; }
		public void setJob(String job) { this.job = job; }
	}

	static void reportMsg(Throwable e) {
		System.err.println (		"Whoops, an exception occurred!\n"+
						"\n*** PLEASE REPORT THIS BUG TO son.gohan.mt2@gmail.com" +
						"\ncopy-pasting the stack trace below." +
						"\n\n*** SEI PREGATO DI SEGNALARE QUESTO BUG A son.gohan.mt2@gmail.com"+
						"\nfacendo un copia-incolla dell'errore sotto riportato."+
						"\n\n------------- FROM HERE -------------");
						e.printStackTrace();
		System.err.println(		"\n------------- TO HERE ---------------\n");
		
		throw new RuntimeException("Program terminated with a RuntimeException");
	}
}
