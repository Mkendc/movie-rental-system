/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package movieRental;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;


public class movieTab extends javax.swing.JFrame {
    Movie_data movieDAO = new Movie_data();
    StaffDAO staffDAO = new StaffDAO();
    CustomerDAO dao = new CustomerDAO();
    
    // Track rented movies that are pending customer assignment
    private java.util.List<Object[]> pendingRentals = new java.util.ArrayList<>();
    
    // Track logged-in staff member
    private Staff currentStaff;

    /**
     * Constructor with Staff parameter for role-based access
     */
    public movieTab(Staff loggedInStaff) {
        this.currentStaff = loggedInStaff;
        initComponents();
        
        // Set window title with user info and role
        if (currentStaff != null) {
            String role = currentStaff.getRole().toUpperCase();
            setTitle("Movie Rental System - " + currentStaff.getFirstName() + " " + 
                     currentStaff.getLastName() + " [" + role + "]");
            
            // Also set the Staff ID in checkout if available
            if (txtStaffID != null) {
                txtStaffID.setText(String.valueOf(currentStaff.getStaffId()));
                txtStaffID.setEditable(false);
            }
        } else {
            setTitle("Movie Rental System");
        }
        
        // Apply role-based restrictions BEFORE loading data
        applyRoleBasedRestrictions();
        
        // Initialize data
        showMovies();
        loadAvailableMovies();  // Load available movies in the first tab
        loadRentedMovies();      // Load rented movies in the first tab
        
        // Only load staff data if user has permission
        if (hasAdminAccess()) {
            loadAllStaff();
        }
        
        loadActiveRentals();     // Load all active rentals in Return tab
        
        // Press Enter to search
        txtSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            loadSearch(keyword);
        });
        
        // Add tab change listener to refresh tabs when selected
        AdminTab.addChangeListener(e -> {
            int selectedIndex = AdminTab.getSelectedIndex();
            if (selectedIndex == 0) { // Movies tab
                loadAvailableMovies(); // Refresh available movies to show current stock
                // Keep rented movies as is (cart items)
            } else if (selectedIndex == 4) { // Return tab
                loadActiveRentals();
            }
        });
    }

    /**
     * Original constructor for backward compatibility
     */
    public movieTab() {
        this(null); // Call with null staff for testing
    }
    
    /**
     * Apply role-based restrictions based on logged-in staff role
     */
    private void applyRoleBasedRestrictions() {
        if (currentStaff == null) {
            // If no staff logged in, apply maximum restrictions
            JOptionPane.showMessageDialog(this, 
                "Warning: No user logged in. Limited access mode.", 
                "Limited Access", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String role = currentStaff.getRole();
        
        if ("Staff".equalsIgnoreCase(role)) {
            // === RESTRICTIONS FOR STAFF ROLE ===
            
            // 1. Remove ADMIN tab which contains Staff and Movie Management
            // The ADMIN tab contains the sub-tabs for staff and movie management
            int adminTabIndex = findTabByName("ADMIN");
            if (adminTabIndex != -1) {
                AdminTab.removeTabAt(adminTabIndex);
                System.out.println("Removed ADMIN tab at index: " + adminTabIndex);
            }
            
            // 2. Disable delete operations in Customer tab
            if (customerRemoveBtn != null) {
                customerRemoveBtn.setEnabled(false);
                customerRemoveBtn.setToolTipText("Only administrators can delete customers");
            }
            
            // 3. Since the entire ADMIN tab is removed, these buttons won't be accessible
            // but we'll still disable them for safety
            if (deleteMovie != null) {
                deleteMovie.setEnabled(false);
                deleteMovie.setVisible(false);
            }
            
            if (deleteStaff != null) {
                deleteStaff.setEnabled(false);
                deleteStaff.setVisible(false);
            }
            
            if (addStaff != null) {
                addStaff.setEnabled(false);
                addStaff.setVisible(false);
            }
            
            if (updateStaff != null) {
                updateStaff.setEnabled(false);
                updateStaff.setVisible(false);
            }
            
            // Show message about restricted access
            System.out.println("Staff access mode activated - Limited permissions applied");
            System.out.println("Available tabs for staff: MOVIES, CUSTOMER, PAYMENT, RETURN");
            
        } else if ("Admin".equalsIgnoreCase(role)) {
            // === FULL ACCESS FOR ADMIN ROLE ===
            System.out.println("Administrator access mode activated - Full permissions granted");
            System.out.println("All tabs available: MOVIES, CUSTOMER, PAYMENT, ADMIN, RETURN");
            // Admin has full access - no restrictions needed
            // All tabs and buttons remain enabled
        } else {
            // Unknown role - apply staff restrictions as safety measure
            JOptionPane.showMessageDialog(this, 
                "Unknown role: " + role + ". Applying limited access.", 
                "Role Warning", 
                JOptionPane.WARNING_MESSAGE);
            // Apply same restrictions as staff
            applyStaffRestrictions();
        }
    }
    
    /**
     * Helper method to find tab index by name
     */
    private int findTabByName(String tabName) {
        for (int i = 0; i < AdminTab.getTabCount(); i++) {
            String title = AdminTab.getTitleAt(i);
            if (title != null && title.toLowerCase().contains(tabName.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Apply staff restrictions (extracted for reuse)
     */
    private void applyStaffRestrictions() {
        // Same restrictions as staff role - remove ADMIN tab
        int adminTabIndex = findTabByName("ADMIN");
        if (adminTabIndex != -1) {
            AdminTab.removeTabAt(adminTabIndex);
        }
        
        if (customerRemoveBtn != null) {
            customerRemoveBtn.setEnabled(false);
            customerRemoveBtn.setToolTipText("Only administrators can delete customers");
        }
    }
    
    /**
     * Check if current user has admin access
     */
    private boolean hasAdminAccess() {
        return currentStaff != null && "Admin".equalsIgnoreCase(currentStaff.getRole());
    }
    
    /**
     * Get current logged-in staff
     */
    public Staff getCurrentStaff() {
        return currentStaff;
    }
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AdminTab = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        logoutMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        userMenu = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        SelectMovieBtn = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAvailableMovies = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRentedMovies = new javax.swing.JTable();
        customerTab = new javax.swing.JPanel();
        customerUpdateBtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        customerRemoveBtn = new javax.swing.JButton();
        customerShowAllBtn = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        lblFirstName = new javax.swing.JLabel();
        customerFirstNameField = new javax.swing.JTextField();
        lblLastName = new javax.swing.JLabel();
        customerLastNameField = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        customerEmailField = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        customerPhoneField = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        customerAddressField = new javax.swing.JTextField();
        customerClearBtn = new javax.swing.JButton();
        customerAddBtn = new javax.swing.JButton();
        customerSearchBtn = new javax.swing.JButton();
        customerSearchField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        pnlCheckout = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblMovieRentalFee = new javax.swing.JTable();
        txtPaymentID = new javax.swing.JTextField();
        txtCustomerID = new javax.swing.JTextField();
        txtStaffID = new javax.swing.JTextField();
        txtReturnedDate = new javax.swing.JTextField();
        txtRentalDate = new javax.swing.JTextField();
        txtReturnDate = new javax.swing.JTextField();
        txtDeposit = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        txtBalance = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        btnCheckout = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jLabel24 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblStaff = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        addStaff = new javax.swing.JButton();
        cmbRole = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        loadSearch = new javax.swing.JTextField();
        searchStaff = new javax.swing.JButton();
        updateStaff = new javax.swing.JButton();
        deleteStaff = new javax.swing.JButton();
        showAllStaff = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        txtTitle = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtGenre = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        txtRating = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        txtFee = new javax.swing.JTextField();
        addMovie = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblMovie = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        searchMovie = new javax.swing.JButton();
        updateMovie = new javax.swing.JButton();
        deleteMovie = new javax.swing.JButton();
        showAllMovie = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        txtReturnSearch = new javax.swing.JTextField();
        btnReturnSearch = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblReturn = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        txtPaymentID1 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txtLateFee = new javax.swing.JTextField();
        lblDaysLate = new javax.swing.JLabel();
        txtDaysLate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        AdminTab.setBackground(new java.awt.Color(26, 65, 70));
        AdminTab.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AdminTab.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        AdminTab.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N

        jPanel6.setBackground(new java.awt.Color(40, 114, 113));

        jPanel7.setBackground(new java.awt.Color(26, 65, 70));

        SelectMovieBtn.setBackground(new java.awt.Color(209, 162, 70));
        SelectMovieBtn.setText("Search Movie");
        SelectMovieBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                performMovieSearch();
            }
        });

        jTextField1.setBackground(new java.awt.Color(26, 65, 70));
        jTextField1.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(230, 222, 199));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(26, 65, 70));
        jLabel2.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(230, 222, 199));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("MOVIE TITLE");

        jLabel1.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(209, 162, 70));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MOVIE TITLE");

        jLabel3.setBackground(new java.awt.Color(26, 65, 70));
        jLabel3.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(230, 222, 199));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("RELEASE YEAR");

        jTextField2.setBackground(new java.awt.Color(26, 65, 70));
        jTextField2.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(230, 222, 199));
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setBackground(new java.awt.Color(26, 65, 70));
        jLabel4.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(230, 222, 199));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel4.setText("GENRE");

        jTextField3.setBackground(new java.awt.Color(26, 65, 70));
        jTextField3.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(230, 222, 199));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setBackground(new java.awt.Color(26, 65, 70));
        jLabel5.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(230, 222, 199));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("RATING");

        jTextField4.setBackground(new java.awt.Color(26, 65, 70));
        jTextField4.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(230, 222, 199));
        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(26, 65, 70));
        jLabel6.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(230, 222, 199));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel6.setText("RENTAL FEE");

        jTextField5.setBackground(new java.awt.Color(26, 65, 70));
        jTextField5.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(230, 222, 199));
        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        // Icon temporarily disabled to prevent NullPointerException
        try {
            java.net.URL iconURL = getClass().getResource("/image-removebg-preview (1) 1.png");
            if (iconURL != null) {
                jLabel8.setIcon(new javax.swing.ImageIcon(iconURL));
            }
        } catch (Exception e) {
            System.out.println("Could not load icon: " + e.getMessage());
        }

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField2))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField4))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField5))
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(SelectMovieBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(124, Short.MAX_VALUE)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(SelectMovieBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
        );

        tblAvailableMovies.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title", "Release yr", "Genre", "Rating", "Rental Fee", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        // Enable table sorting by clicking column headers
        tblAvailableMovies.setAutoCreateRowSorter(true);
        
        // Add tooltip to guide users
        tblAvailableMovies.setToolTipText("Double-click to rent a movie");
        
        // Add double-click event handler for renting movies
        tblAvailableMovies.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed()) {
                    rentMovieFromAvailableTable();
                }
            }
        });
        
        jScrollPane1.setViewportView(tblAvailableMovies);

        jLabel7.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(230, 222, 199));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Rented Movies");

        jLabel9.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(230, 222, 199));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Available Movies");

        tblRentedMovies.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title", "Release yr", "Genre", "Rating", "Rental Fee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        
        // Enable table sorting by clicking column headers
        tblRentedMovies.setAutoCreateRowSorter(true);
        
        // Add tooltip to guide users
        tblRentedMovies.setToolTipText("Double-click to return a movie");
        
        // Add double-click event handler for returning movies
        tblRentedMovies.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed()) {
                    returnMovieFromRentedTable();
                }
            }
        });
        
        jScrollPane2.setViewportView(tblRentedMovies);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 879, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        AdminTab.addTab("MOVIES", jPanel1);

        customerTab.setBackground(new java.awt.Color(41, 114, 114));

        customerUpdateBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerUpdateBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerUpdateBtn.setText("UPDATE");
        customerUpdateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerUpdateBtnActionPerformed(evt);
            }
        });

        customerTable.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        
        // Add double-click event handler for customer selection and payment
        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && !evt.isConsumed() && !pendingRentals.isEmpty()) {
                    processRentalPayment();
                }
            }
        });
        
        customerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "CUSTOMER ID", "FIRST NAME", "LAST NAME", "EMAIL", "PHONE ", "ADDRESS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(customerTable);

        customerRemoveBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerRemoveBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerRemoveBtn.setText("REMOVE");
        customerRemoveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerRemoveBtnActionPerformed(evt);
            }
        });

        customerShowAllBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerShowAllBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerShowAllBtn.setText("SHOW ALL");
        customerShowAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerShowAllBtnActionPerformed(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(26, 65, 70));

        jLabel39.setBackground(new java.awt.Color(230, 222, 199));
        jLabel39.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(230, 222, 199));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("CUSTOMER REGISTRATION FORM");

        lblFirstName.setFont(new java.awt.Font("Sans Serif Collection", 1, 12)); // NOI18N
        lblFirstName.setForeground(new java.awt.Color(209, 162, 70));
        lblFirstName.setText("FIRST NAME");

        customerFirstNameField.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        lblLastName.setFont(new java.awt.Font("Sans Serif Collection", 1, 12)); // NOI18N
        lblLastName.setForeground(new java.awt.Color(209, 162, 70));
        lblLastName.setText("LAST NAME");

        customerLastNameField.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Sans Serif Collection", 1, 12)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(209, 162, 70));
        jLabel40.setText("EMAIL");

        customerEmailField.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Sans Serif Collection", 1, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(209, 162, 70));
        jLabel41.setText("PHONE");

        customerPhoneField.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jLabel42.setFont(new java.awt.Font("Sans Serif Collection", 1, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(209, 162, 70));
        jLabel42.setText("ADDRESS");

        customerAddressField.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerAddressField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerAddressFieldActionPerformed(evt);
            }
        });

        customerClearBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerClearBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerClearBtn.setText("CLEAR");
        customerClearBtn.setMaximumSize(new java.awt.Dimension(83, 27));
        customerClearBtn.setMinimumSize(new java.awt.Dimension(83, 27));
        customerClearBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerClearBtnActionPerformed(evt);
            }
        });

        customerAddBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerAddBtn.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        customerAddBtn.setText("SUBMIT");
        customerAddBtn.setToolTipText("");
        customerAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerAddBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(79, 79, 79)
                        .addComponent(customerClearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(customerAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(customerAddressField)
                    .addComponent(customerPhoneField)
                    .addComponent(customerEmailField)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLastName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFirstName, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(customerLastNameField)
                    .addComponent(customerFirstNameField))
                .addGap(22, 22, 22))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel39)
                .addGap(18, 18, 18)
                .addComponent(lblFirstName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerFirstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblLastName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerLastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerPhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customerAddressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerClearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerAddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(159, Short.MAX_VALUE))
        );

        customerSearchBtn.setBackground(new java.awt.Color(209, 162, 70));
        customerSearchBtn.setText("SEARCH");
        customerSearchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerSearchBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CustomerTabLayout = new javax.swing.GroupLayout(customerTab);
        customerTab.setLayout(CustomerTabLayout);
        CustomerTabLayout.setHorizontalGroup(
            CustomerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CustomerTabLayout.createSequentialGroup()
                        .addComponent(customerSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customerSearchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(customerRemoveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customerUpdateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(customerShowAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        CustomerTabLayout.setVerticalGroup(
            CustomerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CustomerTabLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CustomerTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customerUpdateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerShowAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerRemoveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customerSearchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(CustomerTabLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        AdminTab.addTab("CUSTOMER", customerTab);

        jPanel5.setBackground(new java.awt.Color(26, 65, 70));

        pnlCheckout.setBackground(new java.awt.Color(41, 114, 114));
        pnlCheckout.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel10.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel10.setText("Customer Name:");

        jLabel11.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel11.setText("Payment no.");

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel12.setText("Staff ID:");

        jLabel13.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel13.setText("Rental Date:");

        jLabel14.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel14.setText("Return Date:");

        jLabel15.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel15.setText("Returned Date:");

        jLabel16.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel16.setText("Deposit:");

        jLabel17.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel17.setText("Total Amount:");

        jLabel18.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel18.setText("Remaining Balance:");

        tblMovieRentalFee.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblMovieRentalFee.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tblMovieRentalFee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Movie Name", "Rental Fee"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(tblMovieRentalFee);

        txtPaymentID.setEditable(false);
        txtPaymentID.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        txtCustomerID.setEditable(false);
        txtCustomerID.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        txtStaffID.setEditable(false);
        txtStaffID.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        txtReturnedDate.setEditable(false);
        txtReturnedDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtReturnedDate.setText("MM/DD/YYYY");
        txtReturnedDate.setToolTipText("can be null");

        txtRentalDate.setEditable(false);
        txtRentalDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtRentalDate.setText("MM/DD/YYYY");

        txtReturnDate.setEditable(false);
        txtReturnDate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtReturnDate.setText("MM/DD/YYYY");

        txtDeposit.setEditable(false);
        txtDeposit.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtDeposit.setText("0.00");

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtTotal.setText("0.00");

        txtBalance.setEditable(false);
        txtBalance.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtBalance.setText("0.00");

        jLabel19.setBackground(new java.awt.Color(41, 114, 114));
        jLabel19.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(230, 222, 199));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("CHECKOUT SUMMARY");

        javax.swing.GroupLayout pnlCheckoutLayout = new javax.swing.GroupLayout(pnlCheckout);
        pnlCheckout.setLayout(pnlCheckoutLayout);
        pnlCheckoutLayout.setHorizontalGroup(
            pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCheckoutLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlCheckoutLayout.createSequentialGroup()
                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                .addGap(160, 160, 160)
                                .addComponent(jLabel19))
                            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel13)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlCheckoutLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCheckoutLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDeposit, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                    .addComponent(txtBalance)))
                            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addGap(27, 27, 27)
                                        .addComponent(txtReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(pnlCheckoutLayout.createSequentialGroup()
                                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel12))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtRentalDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                            .addComponent(txtCustomerID)
                                            .addComponent(txtStaffID))))
                                .addGap(41, 41, 41)
                                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtReturnedDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPaymentID, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(71, 71, 71))
        );
        pnlCheckoutLayout.setVerticalGroup(
            pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCheckoutLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtCustomerID)
                    .addComponent(jLabel11)
                    .addComponent(txtPaymentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStaffID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtRentalDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCheckoutLayout.createSequentialGroup()
                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtReturnedDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCheckoutLayout.createSequentialGroup()
                        .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(txtReturnDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(txtDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlCheckoutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(txtBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        btnCheckout.setBackground(new java.awt.Color(26, 65, 70));
        btnCheckout.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(255, 255, 255));
        btnCheckout.setText("Proceed to Checkout");
        btnCheckout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCheckout)
                    .addComponent(pnlCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(643, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(pnlCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCheckout, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        AdminTab.addTab("PAYMENT", jPanel3);

        jPanel4.setBackground(new java.awt.Color(40, 114, 113));

        jLabel24.setFont(new java.awt.Font("Sans Serif Collection", 3, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(230, 222, 199));
        jLabel24.setText("WELCOME, ADMIN!");

        jTabbedPane2.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane2.setFont(new java.awt.Font("Sans Serif Collection", 0, 16)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(26, 65, 70));

        tblStaff.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "First name", "Last name", "Username", "Password", "Role", "Date Created", "Last Updated"
            }
        ));
        tblStaff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblStaffMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblStaff);

        jLabel25.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(230, 222, 199));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("STAFF LIST");

        jPanel10.setBackground(new java.awt.Color(26, 65, 70));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel27.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(209, 162, 70));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("SEARCH STAFF");
        jPanel10.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 13, 323, -1));

        jLabel28.setBackground(new java.awt.Color(26, 65, 70));
        jLabel28.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(230, 222, 199));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel28.setText("FIRST NAME:");
        jPanel10.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 153, 32));

        txtFirstName.setBackground(new java.awt.Color(26, 65, 70));
        txtFirstName.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtFirstName.setForeground(new java.awt.Color(230, 222, 199));
        txtFirstName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });
        jPanel10.add(txtFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 100, 152, 32));

        jLabel29.setBackground(new java.awt.Color(26, 65, 70));
        jLabel29.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(230, 222, 199));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel29.setText("LAST NAME:");
        jPanel10.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 153, 32));

        txtLastName.setBackground(new java.awt.Color(26, 65, 70));
        txtLastName.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtLastName.setForeground(new java.awt.Color(230, 222, 199));
        txtLastName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLastNameActionPerformed(evt);
            }
        });
        jPanel10.add(txtLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 140, 152, 32));

        jLabel31.setBackground(new java.awt.Color(26, 65, 70));
        jLabel31.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(230, 222, 199));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel31.setText("PASSWORD:");
        jPanel10.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 153, 32));

        txtUsername.setBackground(new java.awt.Color(26, 65, 70));
        txtUsername.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(230, 222, 199));
        txtUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel10.add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 152, 32));

        txtPassword.setBackground(new java.awt.Color(26, 65, 70));
        txtPassword.setFont(new java.awt.Font("Sans Serif Collection", 0, 14)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(230, 222, 199));
        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        jPanel10.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 152, 32));

        jLabel43.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(230, 222, 199));
        jLabel43.setText("USERNAME:");
        jPanel10.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 180, -1, 38));

        addStaff.setBackground(new java.awt.Color(209, 162, 70));
        addStaff.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        addStaff.setText("ADD");
        addStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStaffActionPerformed(evt);
            }
        });
        jPanel10.add(addStaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 310, 80, 34));

        cmbRole.setBackground(new java.awt.Color(26, 65, 70));
        cmbRole.setForeground(new java.awt.Color(230, 222, 199));
        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Admin", "Staff" }));
        cmbRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbRoleActionPerformed(evt);
            }
        });
        jPanel10.add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 260, 152, 32));

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(230, 222, 199));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("ROLE:");
        jPanel10.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 260, 100, 32));

        loadSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSearchActionPerformed(evt);
            }
        });

        searchStaff.setBackground(new java.awt.Color(209, 162, 70));
        searchStaff.setText("SEARCH");
        searchStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchStaffActionPerformed(evt);
            }
        });

        updateStaff.setBackground(new java.awt.Color(209, 162, 70));
        updateStaff.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        updateStaff.setText("UPDATE");
        updateStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateStaffActionPerformed(evt);
            }
        });

        deleteStaff.setBackground(new java.awt.Color(209, 162, 70));
        deleteStaff.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        deleteStaff.setText("DELETE");
        deleteStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteStaffActionPerformed(evt);
            }
        });

        showAllStaff.setBackground(new java.awt.Color(209, 162, 70));
        showAllStaff.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        showAllStaff.setText("SHOW ALL");
        showAllStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllStaffActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 956, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(loadSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(searchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(updateStaff)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(deleteStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(showAllStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane4))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showAllStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("STAFF MANAGEMENT", jPanel2);

        jPanel9.setBackground(new java.awt.Color(26, 65, 70));

        jPanel11.setBackground(new java.awt.Color(26, 65, 70));

        txtTitle.setBackground(new java.awt.Color(26, 65, 70));
        txtTitle.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(230, 222, 199));
        txtTitle.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTitleActionPerformed(evt);
            }
        });

        jLabel32.setBackground(new java.awt.Color(26, 65, 70));
        jLabel32.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(230, 222, 199));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel32.setText("MOVIE TITLE:");

        jLabel33.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(209, 162, 70));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("SEARCH MOVIE");

        jLabel34.setBackground(new java.awt.Color(26, 65, 70));
        jLabel34.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(230, 222, 199));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel34.setText("RELEASE YEAR:");

        txtYear.setBackground(new java.awt.Color(26, 65, 70));
        txtYear.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtYear.setForeground(new java.awt.Color(230, 222, 199));
        txtYear.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel35.setBackground(new java.awt.Color(26, 65, 70));
        jLabel35.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(230, 222, 199));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel35.setText("GENRE:");

        txtGenre.setBackground(new java.awt.Color(26, 65, 70));
        txtGenre.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtGenre.setForeground(new java.awt.Color(230, 222, 199));
        txtGenre.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel36.setBackground(new java.awt.Color(26, 65, 70));
        jLabel36.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(230, 222, 199));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel36.setText("RATING:");

        txtRating.setBackground(new java.awt.Color(26, 65, 70));
        txtRating.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtRating.setForeground(new java.awt.Color(230, 222, 199));
        txtRating.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRatingActionPerformed(evt);
            }
        });

        jLabel37.setBackground(new java.awt.Color(26, 65, 70));
        jLabel37.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(230, 222, 199));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel37.setText("RENTAL FEE:");

        txtFee.setBackground(new java.awt.Color(26, 65, 70));
        txtFee.setFont(new java.awt.Font("Sans Serif Collection", 0, 18)); // NOI18N
        txtFee.setForeground(new java.awt.Color(230, 222, 199));
        txtFee.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        addMovie.setBackground(new java.awt.Color(209, 162, 70));
        addMovie.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        addMovie.setText("ADD");
        addMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMovieActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtYear))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtGenre))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtRating))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(addMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtFee))))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtGenre, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRating, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFee, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(addMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblMovie.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title", "Release year", "Genre", "Rating", "Rental Fee", "Stock"
            }
        ));
        
        // Add mouse listener to populate fields when a movie is selected
        tblMovie.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMovieMouseClicked(evt);
            }
        });
        
        jScrollPane5.setViewportView(tblMovie);

        jLabel38.setFont(new java.awt.Font("Sans Serif Collection", 1, 24)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(230, 222, 199));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("MOVIE LIST");

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        searchMovie.setBackground(new java.awt.Color(209, 162, 70));
        searchMovie.setText("SEARCH");
        searchMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchMovieActionPerformed(evt);
            }
        });

        updateMovie.setBackground(new java.awt.Color(209, 162, 70));
        updateMovie.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        updateMovie.setText("UPDATE");
        updateMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateMovieActionPerformed(evt);
            }
        });

        deleteMovie.setBackground(new java.awt.Color(209, 162, 70));
        deleteMovie.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        deleteMovie.setText("DELETE");
        deleteMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMovieActionPerformed(evt);
            }
        });

        showAllMovie.setBackground(new java.awt.Color(209, 162, 70));
        showAllMovie.setFont(new java.awt.Font("Sans Serif Collection", 0, 13)); // NOI18N
        showAllMovie.setText("SHOW ALL");
        showAllMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllMovieActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 957, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(searchMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(updateMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(showAllMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateMovie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showAllMovie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(41, 41, 41))
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("MOVIE MANAGEMENT", jPanel9);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 1303, Short.MAX_VALUE)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE))
        );

        AdminTab.addTab("ADMIN", jPanel4);

        jPanel8.setBackground(new java.awt.Color(26, 65, 70));

        jPanel12.setBackground(new java.awt.Color(41, 114, 114));

        jLabel20.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(230, 222, 199));
        jLabel20.setText("RETURN FORM");

        btnReturnSearch.setBackground(new java.awt.Color(26, 65, 70));
        btnReturnSearch.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnReturnSearch.setForeground(new java.awt.Color(255, 255, 255));
        btnReturnSearch.setText("Search");
        btnReturnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnSearchActionPerformed(evt);
            }
        });

        tblReturn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Rental ID", "Customer Name", "Rental Date", "Total Rental Cost", "Total Paid", "Security Deposit"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblReturn);

        jButton11.setBackground(new java.awt.Color(26, 65, 70));
        jButton11.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("CLEAR");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel21.setForeground(new java.awt.Color(230, 222, 199));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(248, 248, 248))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnReturnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtReturnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 829, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnReturnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtReturnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        jPanel14.setBackground(new java.awt.Color(41, 114, 114));

        jLabel22.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel22.setText("Payment ID");

        txtPaymentID1.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        jButton12.setBackground(new java.awt.Color(26, 65, 70));
        jButton12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("Search");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchByPaymentId();
            }
        });

        jButton13.setBackground(new java.awt.Color(26, 65, 70));
        jButton13.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("PROCESS RETURN");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jLabel23.setText("Late Fees");

        txtLateFee.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        lblDaysLate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblDaysLate.setText("Days Late");

        txtDaysLate.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton13)
                .addGap(42, 42, 42))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(txtDaysLate, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton12)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addComponent(jLabel22)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtPaymentID1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(lblDaysLate)
                        .addGap(18, 18, 18)
                        .addComponent(txtLateFee, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtPaymentID1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12)
                .addGap(62, 62, 62)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDaysLate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDaysLate)
                    .addComponent(txtLateFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(69, 69, 69))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(91, 91, 91))))
        );

        AdminTab.addTab("RETURN", jPanel8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(AdminTab)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(AdminTab)
        );

        AdminTab.getAccessibleContext().setAccessibleDescription("");
        
        // Setup Menu Bar
        fileMenu.setText("File");
        fileMenu.setFont(new java.awt.Font("SansSerif", 0, 14));
        
        logoutMenuItem.setText("Logout");
        logoutMenuItem.setFont(new java.awt.Font("SansSerif", 0, 14));
        logoutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        logoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(logoutMenuItem);
        
        fileMenu.addSeparator();
        
        exitMenuItem.setText("Exit");
        exitMenuItem.setFont(new java.awt.Font("SansSerif", 0, 14));
        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);
        
        menuBar.add(fileMenu);
        
        // User menu showing current logged-in user
        userMenu.setText("User: " + (currentStaff != null ? currentStaff.getFirstName() + " (" + currentStaff.getRole() + ")" : "Unknown"));
        userMenu.setFont(new java.awt.Font("SansSerif", 1, 14));
        userMenu.setEnabled(false);  // Make it non-clickable, just for display
        menuBar.add(javax.swing.Box.createHorizontalGlue()); // Push user menu to the right
        menuBar.add(userMenu);
        
        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void searchStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchStaffActionPerformed
        String keyword = txtSearch.getText();
        loadSearch(keyword);
        clearFields();
    }//GEN-LAST:event_searchStaffActionPerformed

    private void txtLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLastNameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void addStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStaffActionPerformed
    String firstName = txtFirstName.getText();
    String lastName = txtLastName.getText();
    String username = txtUsername.getText();
    String password = txtPassword.getText();
    String role = cmbRole.getSelectedItem().toString();

    if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields!");
        return;
    }

    staffDAO.addStaff(firstName, lastName, username, password, role);
    
    loadAllStaff();
    
    }//GEN-LAST:event_addStaffActionPerformed

    private void deleteStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteStaffActionPerformed
        int row = tblStaff.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a staff to delete!");
        return;
    }

    int id = Integer.parseInt(tblStaff.getValueAt(row, 0).toString());

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this staff?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        staffDAO.deleteStaff(id);
        loadAllStaff();
        clearFields();
    }
    }//GEN-LAST:event_deleteStaffActionPerformed

    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTitleActionPerformed

    private void txtRatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRatingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRatingActionPerformed

    private void deleteMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMovieActionPerformed
        int row = tblMovie.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a movie to delete!");
        return;
    }

    String title = tblMovie.getValueAt(row, 0).toString();
    movieDAO.deleteMovie(title);
    JOptionPane.showMessageDialog(this, "Movie deleted successfully!");
    showMovies();
    }//GEN-LAST:event_deleteMovieActionPerformed

    private void addMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMovieActionPerformed
        try {
        String title = txtTitle.getText();
        int year = Integer.parseInt(txtYear.getText());
        String genre = txtGenre.getText();
        String rating = txtRating.getText().toUpperCase();  // Get rating as String (G, PG, PG-13, R, NC-17)
        double fee = Double.parseDouble(txtFee.getText());
        
        // Ask for stock quantity
        String stockStr = JOptionPane.showInputDialog(this, "Enter stock quantity:", "5");
        int stock = Integer.parseInt(stockStr != null ? stockStr : "1");

        // Validate rating
        if (!isValidRating(rating)) {
            JOptionPane.showMessageDialog(this, "Invalid rating! Use: G, PG, PG-13, R, or NC-17");
            return;
        }

        // Add movie with stock
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
            
            String sql = "INSERT INTO movie (title, release_year, genre, rating, rental_fee, stock_quantity, is_available) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, year);
            stmt.setString(3, genre);
            stmt.setString(4, rating);
            stmt.setDouble(5, fee);
            stmt.setInt(6, stock);
            stmt.setBoolean(7, stock > 0);
            stmt.executeUpdate();
            stmt.close();
            conn.close();
            
            JOptionPane.showMessageDialog(this, "Movie added successfully with " + stock + " copies!");
        } catch (java.sql.SQLException ex) {
            // Fallback to DAO
            Movie movie = new Movie(title, year, genre, rating, fee);
            movieDAO.addMovie(movie);
            JOptionPane.showMessageDialog(this, "Movie added successfully!");
        }
        
        showMovies();
        loadAvailableMovies(); // Refresh available movies too
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_addMovieActionPerformed

    private void updateStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateStaffActionPerformed
        int row = tblStaff.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Select a staff to update!");
        return;
    }

    int id = Integer.parseInt(tblStaff.getValueAt(row, 0).toString());
    String firstName = txtFirstName.getText();
    String lastName = txtLastName.getText();
    String username = txtUsername.getText();
    String password = txtPassword.getText();
    String role = cmbRole.getSelectedItem().toString();

    staffDAO.updateStaff(id, firstName, lastName, username, password, role);
    loadAllStaff();
    clearFields();
    }//GEN-LAST:event_updateStaffActionPerformed

    private void customerAddressFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerAddressFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_customerAddressFieldActionPerformed

    private void searchMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchMovieActionPerformed
        String keyword = txtSearch.getText();
    List<Movie> movies = movieDAO.searchMovies(keyword);
    populateTable(movies);
    }//GEN-LAST:event_searchMovieActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cmbRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbRoleActionPerformed
        // Do nothing - the combo box model is already set in initComponents
        // Removing the reset to prevent issues
    }//GEN-LAST:event_cmbRoleActionPerformed

    private void btnReturnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnSearchActionPerformed
        searchActiveRentals();
    }//GEN-LAST:event_btnReturnSearchActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        clearReturnForm();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-LAST:event_jButton13ActionPerformed
        processReturnWithLateFees();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void showAllStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllStaffActionPerformed
        loadAllStaff();
        clearFields();
    }//GEN-LAST:event_showAllStaffActionPerformed

    private void updateMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateMovieActionPerformed
        int row = tblMovie.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a movie to update!");
            return;
        }

        try {
            String oldTitle = tblMovie.getValueAt(row, 0).toString();
            String title = txtTitle.getText().trim();
            
            // Check if fields are empty
            if (title.isEmpty() || txtYear.getText().isEmpty() || txtGenre.getText().isEmpty() || 
                txtRating.getText().isEmpty() || txtFee.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!");
                return;
            }
            
            int year = Integer.parseInt(txtYear.getText());
            String genre = txtGenre.getText();
            String rating = txtRating.getText().toUpperCase();
            double fee = Double.parseDouble(txtFee.getText());

            // Validate rating
            if (!isValidRating(rating)) {
                JOptionPane.showMessageDialog(this, "Invalid rating! Use: G, PG, PG-13, R, or NC-17");
                return;
            }

            // Also update stock if needed
            int currentRow = tblMovie.getSelectedRow();
            if (currentRow != -1 && tblMovie.getColumnCount() > 5) {
                Object stockObj = tblMovie.getValueAt(currentRow, 5);
                if (stockObj != null) {
                    String stockStr = JOptionPane.showInputDialog(this, 
                        "Update stock quantity (current: " + stockObj + "):", stockObj.toString());
                    if (stockStr != null && !stockStr.isEmpty()) {
                        try {
                            int newStock = Integer.parseInt(stockStr);
                            
                            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
                            
                            String updateStockSQL = "UPDATE movie SET stock_quantity = ?, " +
                                                   "is_available = ? WHERE title = ?";
                            java.sql.PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL);
                            stockStmt.setInt(1, newStock);
                            stockStmt.setBoolean(2, newStock > 0);
                            stockStmt.setString(3, title);
                            stockStmt.executeUpdate();
                            stockStmt.close();
                            conn.close();
                        } catch (Exception ex) {
                            // Ignore stock update error
                        }
                    }
                }
            }
            
            movieDAO.updateMovie(title, year, genre, rating, fee, oldTitle);
            JOptionPane.showMessageDialog(this, "Movie updated successfully!");
            
            // Clear fields after update
            txtTitle.setText("");
            txtYear.setText("");
            txtGenre.setText("");
            txtRating.setText("");
            txtFee.setText("");
            
            showMovies();
            loadAvailableMovies(); // Refresh available movies
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format! Please check Year and Rental Fee fields.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating movie: " + e.getMessage());
        }
    }//GEN-LAST:event_updateMovieActionPerformed

    private void showAllMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllMovieActionPerformed
        showMovies();
    }//GEN-LAST:event_showAllMovieActionPerformed

    private void tblStaffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblStaffMouseClicked
        int row = tblStaff.getSelectedRow();
    txtFirstName.setText(tblStaff.getValueAt(row, 1).toString());
    txtLastName.setText(tblStaff.getValueAt(row, 2).toString());
    txtUsername.setText(tblStaff.getValueAt(row, 3).toString());
    txtPassword.setText(tblStaff.getValueAt(row, 4).toString());
    cmbRole.setSelectedItem(tblStaff.getValueAt(row, 5).toString());
    }//GEN-LAST:event_tblStaffMouseClicked
    
    private void tblMovieMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMovieMouseClicked
        int row = tblMovie.getSelectedRow();
        if (row != -1) {
            // Populate the text fields with selected movie data
            txtTitle.setText(tblMovie.getValueAt(row, 0).toString());
            txtYear.setText(tblMovie.getValueAt(row, 1).toString());
            txtGenre.setText(tblMovie.getValueAt(row, 2).toString());
            txtRating.setText(tblMovie.getValueAt(row, 3).toString());
            
            // Handle rental fee - remove currency symbol if present
            String fee = tblMovie.getValueAt(row, 4).toString();
            fee = fee.replace("₱", "").replace(",", "").trim();
            txtFee.setText(fee);
            
            // Note: Stock is in column 5 but we don't have a field for it in the form yet
        }
    }//GEN-LAST:event_tblMovieMouseClicked

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void loadSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loadSearchActionPerformed

    private void customerRemoveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerRemoveBtnActionPerformed
int selectedRow = customerTable.getSelectedRow();

    // Make sure a row is selected
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get customer ID from table
    int customerId = (int) customerTable.getValueAt(selectedRow, 0);
    String customerName = customerTable.getValueAt(selectedRow, 1) + " " + customerTable.getValueAt(selectedRow, 2);

    // First confirmation
    int confirm1 = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + customerName + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm1 == JOptionPane.YES_OPTION) {
        // Second confirmation
        int confirm2 = JOptionPane.showConfirmDialog(this,
                "This action is permanent. Do you really want to delete " + customerName + "?",
                "Confirm Permanent Delete", JOptionPane.YES_NO_OPTION);

        if (confirm2 == JOptionPane.YES_OPTION) {
            // Perform deletion
            CustomerDAO dao = new CustomerDAO();
            dao.deleteCustomer(customerId);

            // Refresh table
            showAllCustomers();

            // Clear fields (optional)
            clearFields();
        }
    }
    }//GEN-LAST:event_customerRemoveBtnActionPerformed

    private void customerAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerAddBtnActionPerformed
        // Get values from text fields
    String firstName = customerFirstNameField.getText().trim();
    String lastName = customerLastNameField.getText().trim();
    String email = customerEmailField.getText().trim();
    String phone = customerPhoneField.getText().trim();
    String address = customerAddressField.getText().trim();

    // ✅ Validation: Make sure all fields are filled
    if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields before submitting.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Create Customer object
    Customer c = new Customer(firstName, lastName, email, phone, address);

    // Add customer to database
    CustomerDAO dao = new CustomerDAO();
    dao.addCustomer(c);

    // Refresh table
    showAllCustomers();

    // Clear fields after submission
    clearFields();
    }//GEN-LAST:event_customerAddBtnActionPerformed

    private void customerShowAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerShowAllBtnActionPerformed
        showAllCustomers();
    }//GEN-LAST:event_customerShowAllBtnActionPerformed

    private void customerUpdateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerUpdateBtnActionPerformed
        int selectedRow = customerTable.getSelectedRow();

    // Make sure a row is selected
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a customer to update.", "Warning", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Get customer ID from table
    int customerId = (int) customerTable.getValueAt(selectedRow, 0);

    // Get values from text fields
    String firstName = customerFirstNameField.getText().trim();
    String lastName  = customerLastNameField.getText().trim();
    String email     = customerEmailField.getText().trim();
    String phone     = customerPhoneField.getText().trim();
    String address   = customerAddressField.getText().trim();

    // Fetch current values from the selected row if field is empty
    if (firstName.isEmpty()) firstName = (String) customerTable.getValueAt(selectedRow, 1);
    if (lastName.isEmpty())  lastName  = (String) customerTable.getValueAt(selectedRow, 2);
    if (email.isEmpty())     email     = (String) customerTable.getValueAt(selectedRow, 3);
    if (phone.isEmpty())     phone     = (String) customerTable.getValueAt(selectedRow, 4);
    if (address.isEmpty())   address   = (String) customerTable.getValueAt(selectedRow, 5);

    // Create Customer object with updated values
    Customer c = new Customer(firstName, lastName, email, phone, address);
    c.setCustomerId(customerId);

    // Update customer in database
    CustomerDAO dao = new CustomerDAO();
    dao.updateCustomer(c);

    // Refresh table
    showAllCustomers();

    // Clear fields (optional)
    clearFields();
    }//GEN-LAST:event_customerUpdateBtnActionPerformed

    private void customerSearchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerSearchBtnActionPerformed
        String keyword = customerSearchField.getText().trim();
    
    // If empty, show all customers
    if(keyword.isEmpty()) {
        showAllCustomers();
        return;
    }
    
    List<Customer> list = dao.searchCustomer(keyword);
    populateCustomerTable(list);
    }//GEN-LAST:event_customerSearchBtnActionPerformed

    private void customerClearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerClearBtnActionPerformed
        customerFirstNameField.setText("");
    customerLastNameField.setText("");
    customerEmailField.setText("");
    customerPhoneField.setText("");
    customerAddressField.setText("");
    customerSearchField.setText("");

    // Optionally, reset the table to show all customers
    showAllCustomers();
    }//GEN-LAST:event_customerClearBtnActionPerformed

private void showMovies() {
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        String sql = "SELECT title, release_year, genre, rating, rental_fee, stock_quantity " +
                    "FROM movie ORDER BY title";
        
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        java.sql.ResultSet rs = stmt.executeQuery();
        
        DefaultTableModel model = (DefaultTableModel) tblMovie.getModel();
        model.setRowCount(0);
        
        while (rs.next()) {
            Object[] row = {
                rs.getString("title"),
                rs.getInt("release_year"),
                rs.getString("genre"),
                rs.getString("rating"),
                rs.getDouble("rental_fee"),
                rs.getInt("stock_quantity")
            };
            model.addRow(row);
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
    } catch (java.sql.SQLException e) {
        // Fall back to DAO
        List<Movie> movies = movieDAO.getAllMovies();
        populateTable(movies);
    }
}
    
    /**
     * Load available movies into the Available Movies table with stock info
     */
    private void loadAvailableMovies() {
        DefaultTableModel model = (DefaultTableModel) tblAvailableMovies.getModel();
        model.setRowCount(0);
        
        try {
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
            
            String sql = "SELECT title, release_year, genre, rating, rental_fee, stock_quantity, is_available " +
                        "FROM movie WHERE is_available = TRUE AND stock_quantity > 0 " +
                        "ORDER BY title";
            
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getString("title"),
                    rs.getInt("release_year"),
                    rs.getString("genre"),
                    rs.getString("rating"),
                    String.format("₱%.4f", rs.getDouble("rental_fee")),
                    rs.getInt("stock_quantity") + " pcs"
                };
                model.addRow(row);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (java.sql.SQLException e) {
            // Fall back to using DAO if database connection fails
            List<Movie> movies = movieDAO.getAllMovies();
            for (Movie movie : movies) {
                if (movie.getTitle() != null) {
                    model.addRow(new Object[]{
                        movie.getTitle(),
                        movie.getReleaseYear(),
                        movie.getGenre(),
                        movie.getRating(),
                        String.format("₱%.4f", movie.getRentalFee()),
                        "N/A" // Stock not available from DAO
                    });
                }
            }
        }
    }
    
    /**
     * Load rented movies into the Rented Movies table
     */
    private void loadRentedMovies() {
        DefaultTableModel model = (DefaultTableModel) tblRentedMovies.getModel();
        model.setRowCount(0);
        // Initially empty - will be populated when movies are rented
    }
    
    /**
     * Perform movie search using search criteria from the main tab
     */
    private void performMovieSearch() {
        // Get search criteria from main tab fields
        String title = jTextField1.getText().trim();
        String releaseYear = jTextField2.getText().trim();
        String genre = jTextField3.getText().trim();
        String rating = jTextField4.getText().trim();
        String rentalFee = jTextField5.getText().trim();
        
        // If all fields are empty, show all movies
        if (title.isEmpty() && releaseYear.isEmpty() && genre.isEmpty() && rating.isEmpty() && rentalFee.isEmpty()) {
            populateAvailableMoviesTable(movieDAO.getAllMovies());
            javax.swing.JOptionPane.showMessageDialog(this, "Showing all available movies", "Search Results", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Perform search based on criteria
        List<Movie> searchResults = searchMoviesByCriteria(title, releaseYear, genre, rating, rentalFee);
        
        // Update Available Movies table
        populateAvailableMoviesTable(searchResults);
        
        // Show results message
        String message = "Found " + searchResults.size() + " movies matching your search criteria";
        javax.swing.JOptionPane.showMessageDialog(this, message, "Search Results", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Search movies based on multiple criteria
     */
    private List<Movie> searchMoviesByCriteria(String title, String releaseYear, String genre, String rating, String rentalFee) {
        List<Movie> allMovies = movieDAO.getAllMovies();
        List<Movie> results = new java.util.ArrayList<>();
        
        for (Movie movie : allMovies) {
            boolean matches = true;
            
            // Check title (case-insensitive partial match)
            if (!title.isEmpty() && !movie.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matches = false;
            }
            
            // Check release year (exact match)
            if (!releaseYear.isEmpty()) {
                try {
                    int searchYear = Integer.parseInt(releaseYear);
                    if (movie.getReleaseYear() != searchYear) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    // Invalid year format, skip this criteria
                }
            }
            
            // Check genre (case-insensitive partial match)
            if (!genre.isEmpty() && !movie.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                matches = false;
            }
            
            // Check rating (exact match for G, PG, PG-13, R, NC-17)
            if (!rating.isEmpty()) {
                if (!movie.getRating().equalsIgnoreCase(rating)) {
                    matches = false;
                }
            }
            
            // Check rental fee (less than or equal to)
            if (!rentalFee.isEmpty()) {
                try {
                    double searchFee = Double.parseDouble(rentalFee);
                    if (movie.getRentalFee() > searchFee) {
                        matches = false;
                    }
                } catch (NumberFormatException e) {
                    // Invalid fee format, skip this criteria
                }
            }
            
            if (matches) {
                results.add(movie);
            }
        }
        
        // Sort results by title (ascending) since ratings are now categorical
        results.sort((m1, m2) -> m1.getTitle().compareToIgnoreCase(m2.getTitle()));
        
        return results;
    }
    
    /**
     * Populate the Available Movies table with stock information
     */
    private void populateAvailableMoviesTable(List<Movie> movies) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblAvailableMovies.getModel();
        model.setRowCount(0); // Clear existing data
        
        try {
            // Connect to database to get stock information
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
            
            for (Movie movie : movies) {
                // Get stock for this movie from database
                int stockQty = getStockForTitle(movie.getTitle(), conn);
                
                // Only add movies that are available (have stock)
                if (stockQty > 0) {
                    model.addRow(new Object[]{
                        movie.getTitle(),
                        movie.getReleaseYear(),
                        movie.getGenre(),
                        movie.getRating(),
                        String.format("₱%.4f", movie.getRentalFee()),
                        stockQty + " pcs"  // Add stock column
                    });
                }
            }
            
            conn.close();
        } catch (java.sql.SQLException e) {
            // If database connection fails, fall back to showing movies without stock info
            for (Movie movie : movies) {
                model.addRow(new Object[]{
                    movie.getTitle(),
                    movie.getReleaseYear(),
                    movie.getGenre(),
                    movie.getRating(),
                    String.format("₱%.4f", movie.getRentalFee()),
                    "N/A"  // Stock not available
                });
            }
        }
    }
    
    /**
     * Helper method to get stock quantity for a movie title
     */
    private int getStockForTitle(String title, java.sql.Connection conn) {
        try {
            String sql = "SELECT stock_quantity FROM movie WHERE title = ? AND is_available = TRUE";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int stock = rs.getInt("stock_quantity");
                rs.close();
                stmt.close();
                return stock;
            }
            
            rs.close();
            stmt.close();
        } catch (java.sql.SQLException e) {
            // Error getting stock, return 0
        }
        return 0;
    }
    
    private void populateTable(List<Movie> movies) {
    DefaultTableModel model = (DefaultTableModel) tblMovie.getModel();
    model.setRowCount(0);
    for (Movie m : movies) {
        model.addRow(new Object[]{
            m.getTitle(),
            m.getReleaseYear(),
            m.getGenre(),
            m.getRating(),
            m.getRentalFee()
        });
    }
}
    private void loadAllStaff() {
    List<Staff> list = staffDAO.getAllStaff();
    populateTable1(list);
}

private void loadSearch(String keyword) {
    List<Staff> list = staffDAO.searchStaff(keyword);
    populateTable1(list);
}

private void populateTable1(List<Staff> list) {
    String[] cols = {"ID", "First Name", "Last Name", "Username", "Password", "Role", "Date Created", "Last Updated"};
    DefaultTableModel model = new DefaultTableModel(cols, 0);
    for (Staff s : list) {
        model.addRow(new Object[]{
            s.getStaffId(),
            s.getFirstName(),
            s.getLastName(),
            s.getUsername(),
            s.getPassword(),
            s.getRole(),
            s.getDateCreated(),
            s.getLastUpdated()
        });
    }
    tblStaff.setModel(model);
}
private void clearFields() {
    txtFirstName.setText("");
    txtLastName.setText("");
    txtUsername.setText("");
    txtPassword.setText("");
    txtSearch.setText("");
    cmbRole.setSelectedIndex(1); // index 0 = Admin, 1 = Staff (adjust if reversed)
}

private void clearCustomerFields() {
    customerFirstNameField.setText("");
    customerLastNameField.setText("");
    customerEmailField.setText("");
    customerPhoneField.setText("");
    customerAddressField.setText("");
    customerSearchField.setText("");
}
// Helper method to validate movie ratings
private boolean isValidRating(String rating) {
    return rating.equals("G") || rating.equals("PG") || rating.equals("PG-13") || 
           rating.equals("R") || rating.equals("NC-17");
}

/**
 * Handle double-click on Available Movies table to rent a movie
 * Following MOVIE_RENTAL_PATTERNS: Update tables and database
 */
private void rentMovieFromAvailableTable() {
    int selectedRow = tblAvailableMovies.getSelectedRow();
    
    // Check if a row is selected
    if (selectedRow == -1) {
        return;
    }
    
    // Check if the row has all required values (null check for safety)
    if (tblAvailableMovies.getValueAt(selectedRow, 0) == null) {
        return;  // Empty row, ignore click
    }
    
    // Get movie details from the selected row
    String title = tblAvailableMovies.getValueAt(selectedRow, 0).toString();
    String releaseYear = tblAvailableMovies.getValueAt(selectedRow, 1).toString();
    String genre = tblAvailableMovies.getValueAt(selectedRow, 2).toString();
    String rating = tblAvailableMovies.getValueAt(selectedRow, 3).toString();
    String rentalFee = tblAvailableMovies.getValueAt(selectedRow, 4).toString();
    
    // Check if stock column exists and has value
    String stock = "1 pcs";  // Default to 1 if stock column missing
    if (tblAvailableMovies.getColumnCount() > 5 && tblAvailableMovies.getValueAt(selectedRow, 5) != null) {
        stock = tblAvailableMovies.getValueAt(selectedRow, 5).toString();
    }
    
    // Check if stock is available
    int stockQty = Integer.parseInt(stock.replaceAll("[^0-9]", ""));
    if (stockQty <= 0) {
        JOptionPane.showMessageDialog(this,
            "This movie is out of stock!",
            "No Stock Available",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Add ONE copy of the movie to pending rentals list
    Object[] movieData = {title, releaseYear, genre, rating, rentalFee};
    pendingRentals.add(movieData);
    
    // Update the stock display in Available Movies table (decrease by 1)
    DefaultTableModel availableModel = (DefaultTableModel) tblAvailableMovies.getModel();
    int newStock = stockQty - 1;
    
    if (newStock > 0) {
        // Update stock display in the table
        availableModel.setValueAt(newStock + " pcs", selectedRow, 5);
    } else {
        // Remove from Available Movies if stock reaches 0
        availableModel.removeRow(selectedRow);
    }
    
    // Add to Rented Movies table (can have multiple copies of same movie)
    DefaultTableModel rentedModel = (DefaultTableModel) tblRentedMovies.getModel();
    rentedModel.addRow(new Object[] {
        title,
        releaseYear,
        genre,
        rating,
        rentalFee
    });
    
    // Show success message with instruction
    JOptionPane.showMessageDialog(this, 
        "Movie '" + title + "' added to rental cart!\n" +
        "Please go to Customer tab to select a customer and complete payment.",
        "Movie Added to Cart",
        JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Update movie availability status in database
 */
private void updateMovieAvailability(String title, boolean isAvailable) {
    String sql = "UPDATE movie SET is_available = ? WHERE title = ?";
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setBoolean(1, isAvailable);
        stmt.setString(2, title);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Error updating movie availability: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Handle double-click on Rented Movies table to return a movie
 * This is for removing from the pending cart, not for actual returns
 */
private void returnMovieFromRentedTable() {
    int selectedRow = tblRentedMovies.getSelectedRow();
    
    // Check if a row is selected
    if (selectedRow == -1) {
        return;
    }
    
    // Get movie details from the selected row
    String title = tblRentedMovies.getValueAt(selectedRow, 0).toString();
    String releaseYear = tblRentedMovies.getValueAt(selectedRow, 1).toString();
    String genre = tblRentedMovies.getValueAt(selectedRow, 2).toString();
    String rating = tblRentedMovies.getValueAt(selectedRow, 3).toString();
    String rentalFee = tblRentedMovies.getValueAt(selectedRow, 4).toString();
    
    // Remove from pending rentals list
    for (int i = 0; i < pendingRentals.size(); i++) {
        Object[] movie = pendingRentals.get(i);
        if (movie[0].toString().equals(title)) {
            pendingRentals.remove(i);
            break;
        }
    }
    
    // Remove from Rented Movies table (visual only)
    DefaultTableModel rentedModel = (DefaultTableModel) tblRentedMovies.getModel();
    rentedModel.removeRow(selectedRow);
    
    // Return ONE copy back to Available Movies
    DefaultTableModel availableModel = (DefaultTableModel) tblAvailableMovies.getModel();
    
    // Check if this movie already exists in Available Movies
    boolean movieExists = false;
    int existingRow = -1;
    
    for (int i = 0; i < availableModel.getRowCount(); i++) {
        if (availableModel.getValueAt(i, 0).toString().equals(title)) {
            movieExists = true;
            existingRow = i;
            break;
        }
    }
    
    if (movieExists) {
        // Movie already in table, just update stock count
        String currentStock = availableModel.getValueAt(existingRow, 5).toString();
        int stockQty = Integer.parseInt(currentStock.replaceAll("[^0-9]", ""));
        availableModel.setValueAt((stockQty + 1) + " pcs", existingRow, 5);
    } else {
        // Movie not in table (was out of stock), add it back with 1 pc
        availableModel.addRow(new Object[] {
            title,
            releaseYear,
            genre,
            rating,
            rentalFee,
            "1 pcs"
        });
    }
    
    // Show success message
    JOptionPane.showMessageDialog(this, 
        "Movie '" + title + "' has been removed from rental cart.",
        "Movie Removed",
        JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Process actual movie return with late fee calculation
 * This should be called from the Return tab for already rented movies
 */
private void processMovieReturn(int rentalId) {
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        // First get rental details to check if it's late
        String selectSQL = "SELECT rental_id, return_date, customer_id, total_amount " +
                          "FROM rentals WHERE rental_id = ? AND status = 'Active'";
        java.sql.PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
        selectStmt.setInt(1, rentalId);
        java.sql.ResultSet rs = selectStmt.executeQuery();
        
        if (rs.next()) {
            java.sql.Date returnDate = rs.getDate("return_date");
            int customerId = rs.getInt("customer_id");
            double totalAmount = rs.getDouble("total_amount");
            
            // Calculate days late
            long currentTime = System.currentTimeMillis();
            long returnTime = returnDate.getTime();
            long diffInMillis = currentTime - returnTime;
            int daysLate = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            double lateFee = 0.0;
            String message = "";
            
            if (daysLate > 0) {
                // ₱20 per day late fee
                lateFee = daysLate * 20.00;
                message = "This rental is " + daysLate + " days late.\n" +
                         "Late fee: ₱" + String.format("%.4f", lateFee) + "\n\n" +
                         "Do you want to process the return with late fees?";
            } else {
                message = "Process return for this rental?";
            }
            
            // Confirm with user
            int confirm = JOptionPane.showConfirmDialog(this, 
                message,
                "Confirm Return", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update rental record
                String updateSQL = "UPDATE rentals SET " +
                                  "actual_return_date = CURDATE(), " +
                                  "late_fee = ?, " +
                                  "status = 'Returned', " +
                                  "balance_amount = balance_amount + ? " +
                                  "WHERE rental_id = ?";
                java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setDouble(1, lateFee);
                updateStmt.setDouble(2, lateFee);
                updateStmt.setInt(3, rentalId);
                updateStmt.executeUpdate();
                
                // Update movie availability
                String updateMovieSQL = "UPDATE movie m " +
                                       "INNER JOIN rental_items ri ON m.movie_id = ri.movie_id " +
                                       "SET m.is_available = TRUE, m.stock_quantity = m.stock_quantity + 1 " +
                                       "WHERE ri.rental_id = ?";
                java.sql.PreparedStatement movieStmt = conn.prepareStatement(updateMovieSQL);
                movieStmt.setInt(1, rentalId);
                movieStmt.executeUpdate();
                
                // Show success message
                String successMsg = "Return processed successfully!";
                if (lateFee > 0) {
                    successMsg += "\n\nLate fee of ₱" + String.format("%.4f", lateFee) + " has been added to the account.";
                }
                
                JOptionPane.showMessageDialog(this, 
                    successMsg,
                    "Return Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                movieStmt.close();
                updateStmt.close();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "No active rental found with ID: " + rentalId,
                "Rental Not Found",
                JOptionPane.WARNING_MESSAGE);
        }
        
        selectStmt.close();
        rs.close();
        conn.close();
        
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error processing return: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Process rental payment when customer is double-clicked
 */
private void processRentalPayment() {
    // Check if there are movies to rent
    if (pendingRentals.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No movies in rental cart!\nPlease add movies from the Movies tab first.",
            "No Movies to Rent",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Get selected customer
    int selectedRow = customerTable.getSelectedRow();
    if (selectedRow == -1) {
        return;
    }
    
    // Get customer details
    int customerId = (int) customerTable.getValueAt(selectedRow, 0);
    String firstName = customerTable.getValueAt(selectedRow, 1).toString();
    String lastName = customerTable.getValueAt(selectedRow, 2).toString();
    String email = customerTable.getValueAt(selectedRow, 3).toString();
    String phone = customerTable.getValueAt(selectedRow, 4).toString();
    
    // Store customer info for payment processing
    this.currentCustomerId = customerId;
    this.currentCustomerName = firstName + " " + lastName;
    
    // Populate Payment Tab fields
    populatePaymentTab(customerId, firstName + " " + lastName);
    
    // Switch to Payment tab (index 2)
    AdminTab.setSelectedIndex(2);
}

/**
 * Populate the Payment tab with customer and rental information
 */
private void populatePaymentTab(int customerId, String customerName) {
    // Set customer information
    txtCustomerID.setText(customerName); // Using name in the Customer ID field
    
    // Set the actual logged-in staff ID
    if (currentStaff != null) {
        txtStaffID.setText(String.valueOf(currentStaff.getStaffId()));
    } else {
        txtStaffID.setText("1"); // Fallback if no staff logged in (shouldn't happen)
    }
    
    // Set dates - 3 days rental period
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
    java.util.Date currentDate = new java.util.Date();
    java.util.Date returnDate = new java.util.Date(currentDate.getTime() + 3 * 24 * 60 * 60 * 1000L); // 3 days rental
    
    txtRentalDate.setText(dateFormat.format(currentDate));
    txtReturnDate.setText(dateFormat.format(returnDate)); // Shows when movie should be returned (3 days from now)
    txtReturnedDate.setText("Not yet returned"); // Clear indication that movie hasn't been returned
    
    // Generate payment ID
    txtPaymentID.setText("P" + System.currentTimeMillis() / 1000); // Simple unique ID
    
    // Populate movies table and calculate totals
    DefaultTableModel model = (DefaultTableModel) tblMovieRentalFee.getModel();
    model.setRowCount(0); // Clear existing rows
    
    double totalAmount = 0.0;
    double depositAmount = 0.0;
    
    for (Object[] movie : pendingRentals) {
        String title = movie[0].toString();
        String feeStr = movie[4].toString().replace("₱", "").replace(",", "").trim();
        double fee = Double.parseDouble(feeStr);
        
        // Add movie to the table
        model.addRow(new Object[]{title, "₱" + String.format("%.4f", fee)});
        
        totalAmount += fee;
    }
    
    // Calculate security deposit: 40% of rental fee (refundable)
    depositAmount = totalAmount * 0.40; // 40% security deposit
    double remainingBalance = totalAmount - depositAmount; // Remaining balance to pay on return
    
    // Set financial fields with peso sign
    txtTotal.setText("₱" + String.format("%.4f", totalAmount)); // Total rental fee
    txtDeposit.setText("₱" + String.format("%.4f", depositAmount)); // Security deposit (40% - pay now)
    txtBalance.setText("₱" + String.format("%.4f", remainingBalance)); // Remaining balance (60% - pay on return)
}

/**
 * Handle checkout button click from Payment tab
 */
private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {
    // Check if there are movies to process
    if (pendingRentals.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "No movies to checkout!",
            "Empty Cart",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Get total amount from the payment tab (remove peso sign)
    String totalText = txtTotal.getText().replace("₱", "").replace(",", "").trim();
    double totalAmount = Double.parseDouble(totalText);
    
    // Calculate payment breakdown
    double depositAmount = totalAmount * 0.40; // 40% security deposit only
    double totalToPayNow = depositAmount; // Customer only pays deposit now
    
    // Show payment method selection dialog with clear breakdown
    String[] paymentOptions = {"Cash", "Credit Card", "GCash", "PayMaya", "Cancel"};
    String paymentMessage = "=== RENTAL CHECKOUT ===\n\n" +
        "Customer: " + currentCustomerName + "\n" +
        "Rental Period: 3 Days\n" +
        "Return Date: " + txtReturnDate.getText() + "\n\n" +
        "--- Rental Details ---\n" +
        "Total Rental Fee: ₱" + String.format("%.4f", totalAmount) + " (PAY ON RETURN)\n" +
        "Security Deposit (40%): ₱" + String.format("%.4f", depositAmount) + " (PAY NOW)\n" +
        "================================\n" +
        "TOTAL TO PAY NOW: ₱" + String.format("%.4f", totalToPayNow) + " (Deposit Only)\n\n" +
        "--- Upon Return ---\n" +
        "ON TIME (within 3 days):\n" +
        "  Rental Fee: ₱" + String.format("%.4f", totalAmount) + "\n" +
        "  Deposit Refund: ₱" + String.format("%.4f", depositAmount) + "\n" +
        "  Total to Pay: ₱" + String.format("%.4f", totalAmount) + "\n\n" +
        "LATE (after 3 days):\n" +
        "  Rental Fee: ₱" + String.format("%.4f", totalAmount) + "\n" +
        "  Late Fee: ₱20.00 per day (deducted from deposit)\n\n" +
        "Select Payment Method for Deposit:";
    
    int paymentChoice = JOptionPane.showOptionDialog(this,
        paymentMessage,
        "Payment Confirmation",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        paymentOptions,
        paymentOptions[0]);
    
    if (paymentChoice >= 0 && paymentChoice < 4) {
        // Process the payment
        processPayment(currentCustomerId, currentCustomerName, paymentOptions[paymentChoice], totalAmount);
    }
}

/**
 * Process the actual payment and create rental records
 */
private void processPayment(int customerId, String customerName, String paymentMethod, double totalAmount) {
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        // Calculate payment breakdown - customer only pays deposit now
        double depositAmount = totalAmount * 0.40; // 40% security deposit
        // balance_amount = full rental fee (to be paid on return)
        
        // Create rental record with 3 days rental period
        // balance_amount stores the full rental fee (100% to be paid on return)
        String insertRentalSQL = "INSERT INTO rentals (customer_id, staff_id, rental_date, return_date, " +
                                "total_amount, deposit_amount, balance_amount, status, payment_status, late_fee) " +
                                "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 DAY), ?, ?, ?, 'Active', 'Partial', 0.00)";
        
        java.sql.PreparedStatement stmt = conn.prepareStatement(insertRentalSQL, 
            java.sql.Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, customerId);
        // Use actual logged-in staff ID
        int staffId = currentStaff != null ? currentStaff.getStaffId() : 1;
        stmt.setInt(2, staffId);
        stmt.setDouble(3, totalAmount); // Total rental fee
        stmt.setDouble(4, depositAmount); // Security deposit paid (40%)
        stmt.setDouble(5, totalAmount); // Full rental fee due on return
        stmt.executeUpdate();
        
        // Get the generated rental ID
        java.sql.ResultSet rs = stmt.getGeneratedKeys();
        int rentalId = 0;
        if (rs.next()) {
            rentalId = rs.getInt(1);
        }
        
        // Insert rental items
        if (rentalId > 0) {
            // First check if trigger exists and drop it temporarily to avoid conflict
            try {
                java.sql.Statement dropStmt = conn.createStatement();
                dropStmt.execute("DROP TRIGGER IF EXISTS after_rental_insert");
                dropStmt.close();
            } catch (Exception e) {
                // Ignore if trigger doesn't exist
            }
            
            // Insert rental items
            String insertItemSQL = "INSERT INTO rental_items (rental_id, movie_id, rental_price) " +
                                 "SELECT ?, movie_id, rental_fee FROM movie WHERE title = ?";
            java.sql.PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL);
            
            // Also manually update movie availability since trigger is disabled
            String updateMovieSQL = "UPDATE movie SET " +
                                   "stock_quantity = GREATEST(stock_quantity - 1, 0), " +
                                   "times_rented = times_rented + 1, " +
                                   "is_available = CASE WHEN stock_quantity <= 1 THEN FALSE ELSE TRUE END " +
                                   "WHERE title = ?";
            java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateMovieSQL);
            
            for (Object[] movie : pendingRentals) {
                String movieTitle = movie[0].toString();
                
                // Insert rental item
                itemStmt.setInt(1, rentalId);
                itemStmt.setString(2, movieTitle);
                itemStmt.executeUpdate();
                
                // Manually update movie availability
                updateStmt.setString(1, movieTitle);
                updateStmt.executeUpdate();
            }
            
            itemStmt.close();
            updateStmt.close();
            
            // Insert payment record
            String insertPaymentSQL = "INSERT INTO payments (rental_id, customer_id, staff_id, amount, payment_method) " +
                                    "VALUES (?, ?, ?, ?, ?)";
            java.sql.PreparedStatement paymentStmt = conn.prepareStatement(insertPaymentSQL);
            paymentStmt.setInt(1, rentalId);
            paymentStmt.setInt(2, customerId);
            // Use actual logged-in staff ID
            paymentStmt.setInt(3, staffId);
            paymentStmt.setDouble(4, depositAmount); // Only deposit paid now
            paymentStmt.setString(5, paymentMethod);
            paymentStmt.executeUpdate();
            paymentStmt.close();
        }
        
        stmt.close();
        conn.close();
        
        // Show success message with receipt
        String staffName = currentStaff != null ? 
            currentStaff.getFirstName() + " " + currentStaff.getLastName() + " (ID: " + currentStaff.getStaffId() + ")" : 
            "Unknown";
        
        String receiptMessage = "=== RENTAL RECEIPT ===\n\n" +
            "Rental ID: #" + rentalId + "\n" +
            "Customer: " + customerName + "\n" +
            "Processed by: " + staffName + "\n" +
            "Payment Method: " + paymentMethod + "\n\n" +
            "--- Payment Summary ---\n" +
            "Security Deposit Paid: ₱" + String.format("%.4f", depositAmount) + " (40% of rental)\n" +
            "================================\n" +
            "PAID TODAY: ₱" + String.format("%.4f", depositAmount) + " (Deposit Only)\n\n" +
            "--- Return Information ---\n" +
            "Rental Period: 3 Days\n" +
            "Return By: " + txtReturnDate.getText() + "\n\n" +
            "--- Payment Due on Return ---\n" +
            "Rental Fee: ₱" + String.format("%.4f", totalAmount) + "\n" +
            "Deposit Refund: -₱" + String.format("%.4f", depositAmount) + " (if on time)\n" +
            "NET PAYMENT: ₱" + String.format("%.4f", totalAmount) + "\n\n" +
            "Note: Late returns incur ₱20/day fee deducted from deposit\n\n" +
            "Thank you for your business!\n" +
            "Please return movies on time to get your deposit back.";
        
        JOptionPane.showMessageDialog(this,
            receiptMessage,
            "Payment Complete",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Clear pending rentals and reset payment tab
        pendingRentals.clear();
        clearPaymentTab();
        
        // Clear the rented movies table
        DefaultTableModel rentedModel = (DefaultTableModel) tblRentedMovies.getModel();
        rentedModel.setRowCount(0);
        
        // Switch back to Movies tab
        AdminTab.setSelectedIndex(0);
        
        // Refresh the available movies list to show updated stock
        loadAvailableMovies();
        
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error processing payment: " + e.getMessage(),
            "Payment Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Clear the payment tab fields
 */
private void clearPaymentTab() {
    txtCustomerID.setText("");
    txtPaymentID.setText("");
    txtStaffID.setText("");
    txtRentalDate.setText("MM/DD/YYYY");
    txtReturnDate.setText("MM/DD/YYYY");
    txtReturnedDate.setText("MM/DD/YYYY");
    txtTotal.setText("₱0.00");
    txtDeposit.setText("₱0.00");
    txtBalance.setText("₱0.00");
    
    DefaultTableModel model = (DefaultTableModel) tblMovieRentalFee.getModel();
    model.setRowCount(0);
}

/**
 * Load all active rentals when Return tab opens
 */
private void loadActiveRentals() {
    searchActiveRentals();  // Call search with empty search term to get all
}

/**
 * Search and display active rentals in the Return tab
 */
private void searchActiveRentals() {
    String searchTerm = txtReturnSearch.getText().trim();
    
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        String sql = "SELECT r.rental_id, r.customer_id, " +
                    "CONCAT(c.first_name, ' ', c.last_name) AS customer_name, " +
                    "r.rental_date, r.total_amount, r.deposit_amount, r.balance_amount " +
                    "FROM rentals r " +
                    "JOIN customers c ON r.customer_id = c.customer_id " +
                    "WHERE r.status = 'Active' ";
        
        if (!searchTerm.isEmpty()) {
            sql += "AND (c.first_name LIKE ? OR c.last_name LIKE ? OR r.rental_id LIKE ?) ";
        }
        
        sql += "ORDER BY r.rental_date DESC";
        
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        
        if (!searchTerm.isEmpty()) {
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
        }
        
        java.sql.ResultSet rs = stmt.executeQuery();
        
        // Clear existing rows
        DefaultTableModel model = (DefaultTableModel) tblReturn.getModel();
        model.setRowCount(0);
        
        // Populate table with active rentals
        while (rs.next()) {
            Object[] row = {
                rs.getInt("rental_id"),  // Using rental_id instead of customer_id for unique identification
                rs.getString("customer_name"),
                rs.getDate("rental_date"),
                "₱" + String.format("%.4f", rs.getDouble("total_amount")),
                "₱" + String.format("%.4f", rs.getDouble("total_amount") + rs.getDouble("deposit_amount")), // Total paid (rental + deposit)
                "₱" + String.format("%.4f", rs.getDouble("deposit_amount")) // Deposit to refund
            };
            model.addRow(row);
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
        // Add mouse listener to table for selection
        if (tblReturn.getMouseListeners().length == 0) {
            tblReturn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 1) {
                        loadRentalDetailsForReturn();
                    }
                }
            });
        }
        
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error searching rentals: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Search for rental by Payment ID (Rental ID)
 */
private void searchByPaymentId() {
    String paymentIdText = txtPaymentID1.getText().trim();
    
    if (paymentIdText.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter a Payment ID (Rental ID) to search.",
            "No ID Entered",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        int rentalId = Integer.parseInt(paymentIdText);
        
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        // Get rental details
        String sql = "SELECT rental_id, rental_date FROM rentals WHERE rental_id = ? AND status = 'Active'";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, rentalId);
        java.sql.ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            // Calculate days and fees
            java.sql.Date rentalDate = rs.getDate("rental_date");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            
            long diffInMillis = currentDate.getTime() - rentalDate.getTime();
            int totalDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            // Calculate days late (after 3-day grace period)
            int daysLate = Math.max(0, totalDays - 3);
            double lateFee = daysLate * 20.00;
            
            // Display in the correct fields (swapped to match the labels)
            txtLateFee.setText(String.valueOf(daysLate)); // Days late
            txtDaysLate.setText("₱" + String.format("%.4f", lateFee)); // Late fee amount
            
            // Also search and highlight in the table
            searchAndHighlightRental(rentalId);
            
            JOptionPane.showMessageDialog(this,
                "Rental #" + rentalId + " found!\n" +
                "Rental Date: " + rentalDate + "\n" +
                "Days Rented: " + totalDays + "\n" +
                "Days Late: " + daysLate + "\n" +
                "Late Fee: ₱" + String.format("%.4f", lateFee),
                "Rental Found",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "No active rental found with ID: " + rentalId,
                "Rental Not Found",
                JOptionPane.WARNING_MESSAGE);
            
            // Clear the fee fields
            txtLateFee.setText("0");
            txtDaysLate.setText("₱0.00");
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Invalid Payment ID format. Please enter a number.",
            "Invalid Input",
            JOptionPane.ERROR_MESSAGE);
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error searching rental: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Search and highlight a rental in the return table
 */
private void searchAndHighlightRental(int rentalId) {
    DefaultTableModel model = (DefaultTableModel) tblReturn.getModel();
    
    for (int i = 0; i < model.getRowCount(); i++) {
        Object value = model.getValueAt(i, 0); // Rental ID is in first column
        if (value != null && value instanceof Integer) {
            if ((Integer) value == rentalId) {
                // Select and scroll to the row
                tblReturn.setRowSelectionInterval(i, i);
                tblReturn.scrollRectToVisible(tblReturn.getCellRect(i, 0, true));
                break;
            }
        }
    }
}

/**
 * Load rental details when a row is selected in the return table
 */
private void loadRentalDetailsForReturn() {
    int selectedRow = tblReturn.getSelectedRow();
    if (selectedRow == -1) {
        return;
    }
    
    // Get rental ID from the first column
    int rentalId = (int) tblReturn.getValueAt(selectedRow, 0);
    
    try {
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        // Get rental details including rental_date (not return_date)
        String sql = "SELECT rental_id, rental_date FROM rentals WHERE rental_id = ?";
        java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, rentalId);
        java.sql.ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            // Set the payment ID field
            txtPaymentID1.setText(String.valueOf(rentalId));
            
            // Calculate days since rental (not from return_date)
            java.sql.Date rentalDate = rs.getDate("rental_date");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            
            // Simple day calculation - count actual days between dates
            long diffInMillis = currentDate.getTime() - rentalDate.getTime();
            int totalDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            // Debug output to console
            System.out.println("Return calculation debug:");
            System.out.println("  Rental Date: " + rentalDate);
            System.out.println("  Current Date: " + currentDate);
            System.out.println("  Days difference: " + totalDays);
            
            // Calculate days late (after 3-day grace period)
            int daysLate = Math.max(0, totalDays - 3);
            System.out.println("  Days late (after 3-day grace): " + daysLate);
            
            // Calculate late fee: ₱20 per day after the 3-day period
            double lateFee = daysLate * 20.00;
            
            // Display in the correct fields (swapped to match the labels)
            txtLateFee.setText(String.valueOf(daysLate)); // Number of days late goes in txtLateFee (which has "Days Late" label)
            txtDaysLate.setText("₱" + String.format("%.4f", lateFee)); // Late fee amount goes in txtDaysLate (which has "Late Fees" label)
        }
        
        rs.close();
        stmt.close();
        conn.close();
        
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error loading rental details: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Process the return with late fee calculation
 */
private void processReturnWithLateFees() {
    String rentalIdText = txtPaymentID1.getText().trim();
    
    if (rentalIdText.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please select a rental to return from the table.",
            "No Rental Selected",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    try {
        int rentalId = Integer.parseInt(rentalIdText);
        
        java.sql.Connection conn = java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/movie_rental_system", "root", "");
        
        // Get rental details including balance_amount (60% still owed)
        String selectSQL = "SELECT r.rental_id, r.rental_date, r.customer_id, r.total_amount, " +
                          "r.deposit_amount, r.balance_amount, " +
                          "CONCAT(c.first_name, ' ', c.last_name) AS customer_name " +
                          "FROM rentals r " +
                          "JOIN customers c ON r.customer_id = c.customer_id " +
                          "WHERE r.rental_id = ? AND r.status = 'Active'";
        
        java.sql.PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
        selectStmt.setInt(1, rentalId);
        java.sql.ResultSet rs = selectStmt.executeQuery();
        
        if (rs.next()) {
            java.sql.Date rentalDate = rs.getDate("rental_date");
            double totalAmount = rs.getDouble("total_amount");
            double depositAmount = rs.getDouble("deposit_amount");
            double balanceAmount = rs.getDouble("balance_amount"); // 60% still owed
            String customerName = rs.getString("customer_name");
            
            // Calculate total days since rental
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            
            // Simple day calculation - count actual days between dates
            long diffInMillis = currentDate.getTime() - rentalDate.getTime();
            int totalDays = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            // Debug output to console
            System.out.println("Return processing debug:");
            System.out.println("  Rental Date: " + rentalDate);
            System.out.println("  Current Date: " + currentDate);
            System.out.println("  Days difference: " + totalDays);
            
            // Calculate days late (after 3-day grace period)
            int daysLate = Math.max(0, totalDays - 3);
            System.out.println("  Days late (after 3-day grace): " + daysLate);
            
            double lateFee = 0.0;
            String message = "";
            
            if (daysLate > 0) {
                // Late fee: ₱20 per day after the 3-day period
                lateFee = daysLate * 20.00;
                double depositAfterLateFee = Math.max(0, depositAmount - lateFee);
                double totalDue = totalAmount; // Full rental fee is always due
                double netPayment = totalAmount - depositAfterLateFee; // What customer actually pays
                
                message = "=== RETURN SUMMARY (LATE) ===\n\n" +
                         "Customer: " + customerName + "\n" +
                         "Rental ID: #" + rentalId + "\n" +
                         "Rental Date: " + rentalDate + "\n" +
                         "Return Date: " + new java.sql.Date(System.currentTimeMillis()) + "\n" +
                         "Rental Duration: " + totalDays + " days\n" +
                         "Days Late: " + daysLate + " days (after 3-day period)\n\n" +
                         "--- Payment Calculation ---\n" +
                         "Rental Fee Due: ₱" + String.format("%.4f", totalAmount) + "\n" +
                         "Security Deposit: ₱" + String.format("%.4f", depositAmount) + "\n" +
                         "Late Fee: -₱" + String.format("%.4f", lateFee) + " (deducted from deposit)\n" +
                         "Remaining Deposit: ₱" + String.format("%.4f", depositAfterLateFee) + "\n" +
                         "================================\n" +
                         "TOTAL TO COLLECT: ₱" + String.format("%.4f", netPayment) + "\n\n" +
                         "Process this return and collect payment?";
            } else {
                String returnStatus = totalDays <= 3 ? "ON TIME ✔" : "ON TIME (within grace period)";
                double netPayment = totalAmount - depositAmount; // Rental fee minus deposit refund
                
                message = "=== RETURN SUMMARY (ON TIME) ===\n\n" +
                         "Customer: " + customerName + "\n" +
                         "Rental ID: #" + rentalId + "\n" +
                         "Rental Date: " + rentalDate + "\n" +
                         "Return Date: " + new java.sql.Date(System.currentTimeMillis()) + "\n" +
                         "Rental Duration: " + totalDays + " days\n" +
                         "Return Status: " + returnStatus + "\n\n" +
                         "--- Payment Calculation ---\n" +
                         "Rental Fee Due: ₱" + String.format("%.4f", totalAmount) + "\n" +
                         "Deposit Refund: -₱" + String.format("%.4f", depositAmount) + "\n" +
                         "Late Fee: ₱0.00 (returned on time)\n" +
                         "================================\n" +
                         "TOTAL TO COLLECT: ₱" + String.format("%.4f", netPayment) + "\n\n" +
                         "Process this return and collect payment?";
            }
            
            // Confirm with user
            int confirm = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Return",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update rental record
                String updateSQL = "UPDATE rentals SET " +
                                  "actual_return_date = CURDATE(), " +
                                  "late_fee = ?, " +
                                  "status = 'Returned' " +
                                  "WHERE rental_id = ?";
                
                java.sql.PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setDouble(1, lateFee);
                updateStmt.setInt(2, rentalId);
                updateStmt.executeUpdate();
                
                // Update movie availability - count how many of each movie was rented
                String countSQL = "SELECT movie_id, COUNT(*) as count FROM rental_items " +
                                 "WHERE rental_id = ? GROUP BY movie_id";
                java.sql.PreparedStatement countStmt = conn.prepareStatement(countSQL);
                countStmt.setInt(1, rentalId);
                java.sql.ResultSet countRs = countStmt.executeQuery();
                
                // Update each movie's stock by the correct amount
                String updateMovieSQL = "UPDATE movie SET " +
                                       "is_available = TRUE, " +
                                       "stock_quantity = stock_quantity + ? " +
                                       "WHERE movie_id = ?";
                java.sql.PreparedStatement movieStmt = conn.prepareStatement(updateMovieSQL);
                
                while (countRs.next()) {
                    int movieId = countRs.getInt("movie_id");
                    int count = countRs.getInt("count");
                    
                    movieStmt.setInt(1, count); // Increase by the number of copies rented
                    movieStmt.setInt(2, movieId);
                    movieStmt.executeUpdate();
                }
                
                countRs.close();
                countStmt.close();
                
                // Show success message
                double depositAfterFees = Math.max(0, depositAmount - lateFee);
                double netCollected = totalAmount - depositAfterFees;
                
                String successMsg = "=== RETURN PROCESSED ===\n\n" +
                                   "Rental #" + rentalId + " has been returned.\n\n" +
                                   "--- Payment Summary ---\n" +
                                   "Rental Fee: ₱" + String.format("%.4f", totalAmount) + "\n";
                
                if (lateFee > 0) {
                    successMsg += "Late Fee: ₱" + String.format("%.4f", lateFee) + " (from deposit)\n" +
                                 "Deposit After Fees: ₱" + String.format("%.4f", depositAfterFees) + "\n";
                } else {
                    successMsg += "Deposit Refund: ₱" + String.format("%.4f", depositAmount) + "\n";
                }
                
                successMsg += "================================\n" +
                             "NET COLLECTED: ₱" + String.format("%.4f", netCollected) + "\n\n" +
                             "Transaction Complete!";
                JOptionPane.showMessageDialog(this,
                    successMsg,
                    "Return Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form and refresh table
                clearReturnForm();
                searchActiveRentals();
                
                // Refresh the Available Movies table to show updated stock
                loadAvailableMovies();
                
                movieStmt.close();
                updateStmt.close();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "No active rental found with ID: " + rentalId,
                "Rental Not Found",
                JOptionPane.WARNING_MESSAGE);
        }
        
        selectStmt.close();
        rs.close();
        conn.close();
        
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Invalid rental ID format.",
            "Invalid Input",
            JOptionPane.ERROR_MESSAGE);
    } catch (java.sql.SQLException e) {
        JOptionPane.showMessageDialog(this,
            "Error processing return: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

/**
 * Clear the return form fields
 */
private void clearReturnForm() {
    txtReturnSearch.setText("");
    txtPaymentID1.setText("");
    txtLateFee.setText("0");      // This field shows days late (has "Days Late" label)
    txtDaysLate.setText("₱0.00"); // This field shows late fee amount (has "Late Fees" label)
    
    // Clear table selection
    if (tblReturn != null) {
        tblReturn.clearSelection();
    }
}

private void showAllCustomers() {
    List<Customer> list = dao.getAllCustomers();
    populateCustomerTable(list);
}

private void populateCustomerTable(List<Customer> list) {
    DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
    model.setRowCount(0);
    for (Customer c : list) {
        model.addRow(new Object[]{
            c.getCustomerId(),
            c.getFirstName(),
            c.getLastName(),
            c.getEmail(),
            c.getPhone(),
            c.getAddress(),
            c.getDateCreated(),
            c.getLastUpdated()
        });
    }
}







 


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(movieTab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(movieTab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(movieTab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(movieTab.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new movieTab().setVisible(true);
            }
        });
    }

    // Custom fields for payment processing
    private int currentCustomerId;
    private String currentCustomerName;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane AdminTab;
    private javax.swing.JButton SelectMovieBtn;
    private javax.swing.JButton addMovie;
    private javax.swing.JButton addStaff;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnReturnSearch;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JButton customerAddBtn;
    private javax.swing.JTextField customerAddressField;
    private javax.swing.JButton customerClearBtn;
    private javax.swing.JTextField customerEmailField;
    private javax.swing.JTextField customerFirstNameField;
    private javax.swing.JTextField customerLastNameField;
    private javax.swing.JTextField customerPhoneField;
    private javax.swing.JButton customerRemoveBtn;
    private javax.swing.JButton customerSearchBtn;
    private javax.swing.JTextField customerSearchField;
    private javax.swing.JButton customerShowAllBtn;
    private javax.swing.JTable customerTable;
    private javax.swing.JButton customerUpdateBtn;
    private javax.swing.JPanel customerTab;
    private javax.swing.JButton deleteMovie;
    private javax.swing.JButton deleteStaff;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JLabel lblDaysLate;
    private javax.swing.JLabel lblFirstName;
    private javax.swing.JLabel lblLastName;
    private javax.swing.JTextField loadSearch;
    private javax.swing.JPanel pnlCheckout;
    private javax.swing.JButton searchMovie;
    private javax.swing.JButton searchStaff;
    private javax.swing.JButton showAllMovie;
    private javax.swing.JButton showAllStaff;
    private javax.swing.JTable tblAvailableMovies;
    private javax.swing.JTable tblMovie;
    private javax.swing.JTable tblMovieRentalFee;
    private javax.swing.JTable tblRentedMovies;
    private javax.swing.JTable tblReturn;
    private javax.swing.JTable tblStaff;
    private javax.swing.JTextField txtBalance;
    private javax.swing.JTextField txtCustomerID;
    private javax.swing.JTextField txtDaysLate;
    private javax.swing.JTextField txtDeposit;
    private javax.swing.JTextField txtFee;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtGenre;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtLateFee;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtPaymentID;
    private javax.swing.JTextField txtPaymentID1;
    private javax.swing.JTextField txtRating;
    private javax.swing.JTextField txtRentalDate;
    private javax.swing.JTextField txtReturnDate;
    private javax.swing.JTextField txtReturnSearch;
    private javax.swing.JTextField txtReturnedDate;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtStaffID;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtUsername;
    private javax.swing.JTextField txtYear;
    private javax.swing.JButton updateMovie;
    private javax.swing.JButton updateStaff;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem logoutMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu userMenu;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Handle logout action
     */
    private void logoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?\n" +
            "You will be returned to the login screen.",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Log the logout action
            System.out.println("User " + currentStaff.getUsername() + " logged out.");
            
            // Close current window
            this.dispose();
            
            // Open login window
            SwingUtilities.invokeLater(() -> {
                new LoginForm1().setVisible(true);
            });
        }
    }
    
    /**
     * Handle exit action
     */
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}
