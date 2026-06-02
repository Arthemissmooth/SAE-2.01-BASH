package fr.iut.robotmineur;

import assets.AssetManager;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class FenetreSimulationAutomatique extends JFrame {


    private static final Color BG_DARK      = new Color(10, 14, 26);
    private static final Color BG_PANEL     = new Color(0, 20, 42);
    private static final Color BG_CARD      = new Color(22, 30, 56);
    private static final Color BG_CARD_HOV  = new Color(28, 40, 72);
    private static final Color ACCENT       = new Color(56, 189, 248);
    private static final Color ACCENT2      = new Color(250, 204, 21);
    private static final Color ACCENT3      = new Color(74, 222, 128);
    private static final Color TEXT_PRIMARY = new Color(226, 232, 240);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color BORDER_COL   = new Color(30, 41, 59);
    private static final Color WHITE = new Color(255, 255, 255);

    private static final Font FONT_TITLE = new Font("Courier New", Font.BOLD, 22);
    private static final Font FONT_H2    = new Font("Courier New", Font.BOLD, 13);
    private static final Font FONT_MONO  = new Font("Courier New", Font.PLAIN, 12);
    private static final Font FONT_LABEL = new Font("Courier New", Font.PLAIN, 11);
    private static final Font FONT_BADGE = new Font("Courier New", Font.BOLD, 11);

    private final Monde monde;

    private JPanel panneauGrille;
    private JPanel zoneInfos;
    private JLabel labelTour;
    private JLabel labelStatus;

    private JComboBox<Robot> comboRobots;
    private JComboBox<String> comboActions;
    private JComboBox<Direction> comboDirections;

    private Robot robotActif;
    private Timer timerSimulation;
    private int delaiEntrerTours = 1000 ;
    private SimulationAutomatique simulationAutomatique;

    public FenetreSimulationAutomatique(Monde monde) {
        this.monde = monde;

        this.simulationAutomatique = new SimulationAutomatique(monde);

        this.timerSimulation = new Timer(delaiEntrerTours, e -> {

            if (simulationAutomatique.estTerminee()) {
                timerSimulation.stop();
                setStatus("Simulation terminée : toutes les mines sont vides.");
                return;
            }

            simulationAutomatique.jouerTour();
            rafraichir();
        });

        setTitle("robots mineurs Simulateur");
        setSize(1380, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
        timerSimulation.start();

        rafraichir();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(8, 12, 28),
                        getWidth(), 0, new Color(14, 20, 44)
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                g2.dispose();
            }
        };

        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 24, 14, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Courier New", Font.BOLD, 14));
        dot.setForeground(ACCENT3);

        JLabel title = new JLabel("MINER-BOT");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("  SIMULATEUR DE ROBOTS MINEURS");
        subtitle.setFont(FONT_LABEL);
        subtitle.setForeground(TEXT_MUTED);

        left.add(dot);
        left.add(title);
        left.add(subtitle);

        labelTour = new JLabel();
        labelTour.setFont(new Font("Courier New", Font.BOLD, 14));
        labelTour.setForeground(ACCENT);
        labelTour.setBorder(new CompoundBorder(
                new LineBorder(ACCENT, 1),
                new EmptyBorder(6, 16, 6, 16)
        ));

        header.add(left, BorderLayout.WEST);
        header.add(labelTour, BorderLayout.EAST);

        return header;
    }

    private JPanel buildCenter() {
        JPanel centre = new JPanel(new BorderLayout(12, 0));
        centre.setBackground(BG_DARK);
        centre.setBorder(new EmptyBorder(16, 16, 20, 16));

        JPanel gridWrapper = new JPanel(new BorderLayout()) {

            private final Image background;

            {
                background = AssetManager.BACKGROUND.getImage();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);

                g2.setColor(new Color(0, 0, 0, 45));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                g2.dispose();
            }
        };

        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));

        panneauGrille = new JPanel(new GridLayout(10, 10, 3, 3));
        panneauGrille.setPreferredSize(new Dimension(645, 260));
        panneauGrille.setOpaque(false);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(panneauGrille);

        gridWrapper.add(centerWrapper, BorderLayout.CENTER);

        JPanel side = buildSidePanel();
        side.setPreferredSize(new Dimension(300, 0));

        centre.add(gridWrapper, BorderLayout.CENTER);
        centre.add(side, BorderLayout.EAST);

        return centre;
    }

    private JPanel buildSidePanel() {
        JPanel side = new JPanel(new BorderLayout(0, 10));
        side.setBackground(BG_DARK);

        zoneInfos = new JPanel();
        zoneInfos.setLayout(new BoxLayout(zoneInfos, BoxLayout.Y_AXIS));
        zoneInfos.setBackground(BG_PANEL);
        zoneInfos.setBorder(new EmptyBorder(12, 10, 12, 10));

        JScrollPane scroll = new JScrollPane(zoneInfos);
        scroll.setBorder(new LineBorder(BORDER_COL, 1));
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JLabel sideTitle = new JLabel("JOURNAL DE BORD");
        sideTitle.setFont(FONT_H2);
        sideTitle.setForeground(ACCENT);
        sideTitle.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(0, 4, 8, 4)
        ));

        side.add(sideTitle, BorderLayout.NORTH);
        side.add(scroll, BorderLayout.CENTER);

        return side;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(BG_PANEL);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, 0, getWidth(), 0);

                g2.dispose();
            }
        };

        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel cmdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        cmdPanel.setOpaque(false);

        JPanel BoutonRecommencer = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        BoutonRecommencer.setOpaque(false);

        JLabel BoutonRecommencerTitle = new JLabel("Recommencer");
        BoutonRecommencer.setFont(FONT_H2);
        BoutonRecommencer.setBackground(BG_PANEL);

        JPanel Sortir = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        Sortir.setOpaque(false);
        Sortir.setBackground(BG_PANEL);
        Sortir.add(BoutonRecommencerTitle);


        JLabel cmdLabel = new JLabel("CMD ›");
        cmdLabel.setFont(FONT_H2);
        cmdLabel.setForeground(ACCENT);

        comboRobots = buildStyledCombo();
        for (Robot r : monde.getRobots()) {
            comboRobots.addItem(r);
        }

        comboActions = buildStyledCombo("AVANCER", "RECOLTER", "DEPOSER", "ATTENDRE");
        comboDirections = buildStyledCombo(Direction.values());

        cmdPanel.add(cmdLabel);
        cmdPanel.add(buildFieldGroup("ROBOT", comboRobots));
        cmdPanel.add(buildFieldGroup("ACTION", comboActions));
        cmdPanel.add(buildFieldGroup("DIRECTION", comboDirections));

        JButton btnValider = buildActionButton("EXÉCUTER", ACCENT, new Color(0, 60, 100));
        JButton btnTour = buildActionButton("TOUR SUIVANT", ACCENT2, new Color(80, 60, 0));

        btnValider.addActionListener(e -> executerAction());
        btnTour.addActionListener(e -> {
            monde.tourSuivant();
            rafraichir();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnValider);
        btnPanel.add(btnTour);






        footer.add(cmdPanel, BorderLayout.WEST);
        footer.add(btnPanel, BorderLayout.EAST);

        labelStatus = new JLabel("› Système initialisé");
        labelStatus.setFont(FONT_LABEL);
        labelStatus.setForeground(ACCENT3);
        labelStatus.setBorder(new EmptyBorder(10, 5, 0, 5));
        footer.add(labelStatus, BorderLayout.SOUTH);

        btnPanel.removeAll();
        cmdPanel.removeAll();

        btnPanel.revalidate();
        btnPanel.repaint();
        cmdPanel.revalidate();
        cmdPanel.repaint();

        return footer;
    }

    @SuppressWarnings("unchecked")
    private <T> JComboBox<T> buildStyledCombo(T... items) {
        JComboBox<T> cb = new JComboBox<>(items);
        styleCombo(cb);
        return cb;
    }

    private <T> JComboBox<T> buildStyledCombo() {
        JComboBox<T> cb = new JComboBox<>();
        styleCombo(cb);
        return cb;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(BG_CARD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_MONO);
        cb.setBorder(new LineBorder(BORDER_COL, 1));
        cb.setPreferredSize(new Dimension(140, 30));

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean selected,
                    boolean focus
            ) {
                super.getListCellRendererComponent(list, value, index, selected, focus);
                setBackground(selected ? BG_CARD_HOV : BG_CARD);
                setForeground(selected ? ACCENT : TEXT_PRIMARY);
                setFont(FONT_MONO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }

    private JPanel buildFieldGroup(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 3));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BADGE);
        lbl.setForeground(TEXT_MUTED);

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    private JButton buildActionButton(String text, Color fg, Color bgDark) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(bgDark);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.setColor(fg);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BADGE);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));

        return btn;
    }

    private void afficherGrille() {
        panneauGrille.removeAll();

        for (int ligne = 0; ligne < 10; ligne++) {
            for (int colonne = 0; colonne < 10; colonne++) {
                panneauGrille.add(creerCase(
                        monde.getSecteur(new Position(ligne, colonne)),
                        ligne,
                        colonne
                ));
            }
        }

        panneauGrille.revalidate();
        panneauGrille.repaint();
    }

    private JPanel creerCase(Secteur secteur, int row, int col) {

        FenetreSimulationAutomatique.CasePanel panel = new FenetreSimulationAutomatique.CasePanel(
                secteur.estEau() ? AssetManager.EAU : AssetManager.TERRAIN
        );

        boolean actif = secteur.getRobot() != null && secteur.getRobot().equals(robotActif);
        panel.setActif(actif);
        panel.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel coord = new JLabel(" " + row + "," + col);
        coord.setFont(new Font("Courier New", Font.PLAIN, 7));
        coord.setForeground(new Color(80, 100, 140));

        JLabel haut = new JLabel("", SwingConstants.RIGHT);
        haut.setFont(new Font("Courier New", Font.BOLD, 7));

        topBar.add(coord, BorderLayout.WEST);
        topBar.add(haut, BorderLayout.EAST);

        JLabel center = new JLabel();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel bottom = new JLabel();
        bottom.setHorizontalAlignment(SwingConstants.CENTER);

        if (secteur.getMine() != null) {
            boolean or = secteur.getMine().getTypeMinerai() == TypeMinerai.OR;

            panel = new FenetreSimulationAutomatique.CasePanel(or ? AssetManager.MINEOR : AssetManager.MINENICKEL);
            panel.setActif(actif);
            panel.setLayout(new BorderLayout());

            haut.setText((or ? "OR" : "NICKEL") + " ");
            haut.setForeground(or ? ACCENT2 : new Color(160, 200, 240));
        }

        if (secteur.getEntrepot() != null) {
            center.setIcon(resize(AssetManager.ENTREPOT, 32, 32));
            haut.setText("DÉPÔT ");
            haut.setForeground(ACCENT3);
        }

        if (secteur.getRobot() != null) {
            boolean or = secteur.getRobot().getTypeMinerai() == TypeMinerai.OR;
            bottom.setIcon(resize(or ? AssetManager.ROBOTOR : AssetManager.ROBOTNICKEL, 28, 28));

            if (haut.getText().trim().isEmpty()) {
                haut.setText((or ? "ROBOT OR" : "ROBOT NK") + " ");
                haut.setForeground(or ? ACCENT2 : ACCENT);
            }
        }

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private class CasePanel extends JPanel {

        private final Image img;
        private boolean actif = false;

        public CasePanel(ImageIcon icon) {
            this.img = icon.getImage();
            setOpaque(false);
        }

        public void setActif(boolean actif) {
            this.actif = actif;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);

            if (actif) {
                g2.setColor(new Color(250, 204, 21, 70));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(250, 204, 21));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }

            g2.dispose();
        }
    }

    private void afficherInfos() {
        zoneInfos.removeAll();

        int scoreTotal = monde.getRobots().stream()
                .mapToInt(Robot::getStockActuel)
                .sum();

        zoneInfos.add(buildGlobalScorePanel(scoreTotal));
        zoneInfos.add(Box.createVerticalStrut(10));

        addSectionHeader("ROBOTS ACTIFS", monde.getRobots().size());
        for (Robot r : monde.getRobots()) {
            zoneInfos.add(buildRobotCard(r));
        }

        zoneInfos.add(Box.createVerticalStrut(10));

        addSectionHeader("MINES", monde.getMines().size());
        for (Mine m : monde.getMines()) {
            zoneInfos.add(buildMineCard(m));
        }

        zoneInfos.add(Box.createVerticalStrut(10));

        addSectionHeader("ENTREPÔTS", monde.getEntrepots().size());
        for (Entrepot e : monde.getEntrepots()) {
            zoneInfos.add(buildEntrepotCard(e));
        }

        zoneInfos.add(Box.createVerticalGlue());
        zoneInfos.revalidate();
        zoneInfos.repaint();
    }

    private JPanel buildGlobalScorePanel(int scoreTotal) {
        JPanel card = buildBaseCard(ACCENT3);

        JLabel score = new JLabel("SCORE TOTAL : " + scoreTotal + " pts");
        score.setFont(FONT_H2);
        score.setForeground(ACCENT3);

        card.add(score, BorderLayout.CENTER);

        return wrapCard(card);
    }

    private JPanel buildRobotCard(Robot r) {
        boolean isOr = r.getTypeMinerai() == TypeMinerai.OR;

        JPanel card = buildBaseCard(ACCENT);

        JLabel texte = new JLabel(
                "Robot #" + r.getId()
                        + " - " + r.getNom()
                        + " - " + (isOr ? "OR" : "NICKEL")
                        + " - Stock : " + r.getStockActuel() + "/" + r.getCapaciteStockage()
        );

        texte.setFont(FONT_LABEL);
        texte.setForeground(TEXT_PRIMARY);

        card.add(texte, BorderLayout.CENTER);

        return wrapCard(card);
    }

    private JPanel buildMineCard(Mine m) {
        boolean isOr = m.getTypeMinerai() == TypeMinerai.OR;

        JPanel card = buildBaseCard(isOr ? ACCENT2 : ACCENT);

        JLabel texte = new JLabel(
                "Mine #" + m.getId()
                        + " - " + (isOr ? "OR" : "NICKEL")
                        + " - " + m.getMineraiRestant() + "/" + m.getQuantiteInitiale()
        );

        texte.setFont(FONT_LABEL);
        texte.setForeground(TEXT_PRIMARY);

        card.add(texte, BorderLayout.CENTER);

        return wrapCard(card);
    }

    private JPanel buildEntrepotCard(Entrepot e) {
        JPanel card = buildBaseCard(ACCENT3);

        JLabel texte = new JLabel(
                "Entrepôt #" + e.getId()
                        + " - " + e.getTypeMinerai()
                        + " - Stock : " + e.getStock()
        );

        texte.setFont(FONT_LABEL);
        texte.setForeground(TEXT_PRIMARY);

        card.add(texte, BorderLayout.CENTER);

        return wrapCard(card);
    }

    private JPanel buildBaseCard(Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.setColor(accentColor);
                g2.fillRect(0, 0, 3, getHeight());

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 10));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        return card;
    }

    private JPanel wrapCard(JPanel card) {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(card);
        wrapper.add(Box.createVerticalStrut(5));
        return wrapper;
    }

    private void addSectionHeader(String label, int count) {
        JLabel lbl = new JLabel(label + " (" + count + ")");
        lbl.setFont(FONT_H2);
        lbl.setForeground(TEXT_MUTED);
        lbl.setBorder(new EmptyBorder(8, 0, 4, 0));
        zoneInfos.add(lbl);
    }

    private void executerAction() {
        Robot r = (Robot) comboRobots.getSelectedItem();



        if (r == null) {
            return;
        }

        String action = (String) comboActions.getSelectedItem();
        robotActif = r;

        if ("AVANCER".equals(action)) {
            boolean ok = monde.deplacerRobot(r, (Direction) comboDirections.getSelectedItem());

            if (ok) {
                setStatus("Robot #" + r.getId() + " déplacé → " + comboDirections.getSelectedItem());
            } else {
                setStatus("Déplacement impossible.");
            }

        } else if ("RECOLTER".equals(action)) {
            Secteur s = monde.getSecteur(r.getPosition());

            if (s.getMine() != null && r.recolter(s.getMine())) {
                setStatus("Robot #" + r.getId() + " a récolté.");
            } else {
                setStatus("Récolte impossible.");
            }

        } else if ("DEPOSER".equals(action)) {
            Secteur s = monde.getSecteur(r.getPosition());

            if (s.getEntrepot() != null && r.deposer(s.getEntrepot())) {
                setStatus("Robot #" + r.getId() + " a déposé.");
            } else {
                setStatus("Dépôt impossible.");
            }

        } else {
            setStatus("Robot #" + r.getId() + " attend.");
        }

        rafraichir();
    }

    private void setStatus(String msg) {
        if (labelStatus != null) {
            labelStatus.setText("› " + msg);
        }
    }

    private ImageIcon resize(ImageIcon icon, int w, int h) {
        return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    private void rafraichir() {
        labelTour.setText(" TOUR " + monde.getTourActuel());
        afficherGrille();
        afficherInfos();
    }
}

