/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dbl_uts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author M. HASNAN AL ABIYYU
 */
public final class Peminjaman extends javax.swing.JFrame {

    private final Connection connection = ConnectDb.getConnectDb().getConnection();
    private String tgl;

    /**
     * Creates new form Peminjaman
     */
    public Peminjaman() {
        initComponents();
        tableModel();
        id_auto();
        txtidbuku.requestFocus();
    }

    public final void id_auto() {
        try {
            final Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            final String sql = "select max (right(id_peminjaman, 1)) as no from peminjaman";
            final ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                if (rs.first() == false) {
                    txtidpeminjaman.setText("PSN0001");
                } else {
                    rs.last();
                    int set_id = rs.getInt(1) + 1;
                    String no = String.valueOf(set_id);
                    int id_next = no.length();
                    for (int a = 0; a < 4 - id_next; a++) {
                        no = "0" + no;
                    }
                    txtidpeminjaman.setText("PSN" + no);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hitungTotalStok() {
        int totalStok = 0;
        int jumlahBaris = tblpeminjaman.getRowCount();  // Mengambil jumlah baris yang masih ada

        // Loop untuk menghitung stok dari setiap baris yang ada di JTable
        for (int i = 0; i < jumlahBaris; i++) {
            // Cek apakah kolom yang berisi stok memiliki nilai yang valid
            if (tblpeminjaman.getValueAt(i, 5) != null) {
                try {
                    // Mengambil stok dari kolom yang sesuai (misal kolom ke-6)
                    int stok = Integer.parseInt(tblpeminjaman.getValueAt(i, 5).toString());
                    totalStok += stok;  // Menambahkan stok ke total
                } catch (NumberFormatException e) {
                    // Menangani kesalahan jika format angka salah
                    System.out.println("Format stok salah di baris: " + i);
                }
            }
        }

        // Menampilkan total stok pada komponen UI (misalnya TextField)
        txttotalygdipinjam.setText("" + totalStok);
    }

    public void loadData() {

        DefaultTableModel model = (DefaultTableModel) tblpeminjaman.getModel();
        model.addRow(new Object[]{
            txtidpeminjaman.getText(),
            txtidanggota.getText(),
            txttanggal.getText(),
            txtidbuku.getText(),
            txtjudulbuku.getText(),
            txtstokbukudipinjam.getText()
        });

    }

    public void clearTextField() {
        txtidbuku.setText("");
        txtjudulbuku.setText("");
        txtstokbukudipinjam.setText("");
    }

    public void tambah() {
        loadData();
        clearTextField();
        hitungTotalStok();
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tblpeminjaman.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }

        txtidanggota.setText("");
    }

    public void tableModel() {
        DefaultTableModel model = new DefaultTableModel();

        tblpeminjaman.setModel(model);

        model.addColumn("Id Peminjaman");
        model.addColumn("Id Anggota");
        model.addColumn("tanggal pinjam");
        model.addColumn("id buku");
        model.addColumn("judul buku");
        model.addColumn("stok yang dipinjam");

        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");

        txttanggal.setText(s.format(date));
    }

    private void lookupBuku() {
        String idBuku = txtidbuku.getText().trim();
        String sql = "SELECT judul_buku FROM buku WHERE id_buku = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, idBuku);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Jika buku ditemukan, ambil judulnya
                String judulBuku = rs.getString("judul_buku");
                txtjudulbuku.setText(judulBuku);
            } else {
                // Jika tidak ditemukan, tampilkan pesan
                txtjudulbuku.setText("Buku tidak ditemukan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void lookupAnggota() {
        String idAnggota = txtidanggota.getText().trim();
        String sql = "SELECT nama FROM anggota WHERE id_anggota = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, idAnggota);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Jika buku ditemukan, ambil judulnya
                String nama = rs.getString("nama");
                txtnamaanggota.setText(nama);
            } else {
                // Jika tidak ditemukan, tampilkan pesan
                txtnamaanggota.setText("Anggota tidak terdaftar");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnriwayatpeminjaman = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtidpeminjaman = new javax.swing.JTextField();
        txtidanggota = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtjudulbuku = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtstokbukudipinjam = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblpeminjaman = new javax.swing.JTable();
        btnhapus = new javax.swing.JButton();
        btnsimpan = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txttotalygdipinjam = new javax.swing.JTextField();
        btntambah = new javax.swing.JButton();
        txttanggal = new javax.swing.JTextField();
        txtidbuku = new javax.swing.JTextField();
        txtnamaanggota = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnback = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 153));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("PEMINJAMAN BUKU");

        btnriwayatpeminjaman.setText("Riwayat Peminjaman");
        btnriwayatpeminjaman.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnriwayatpeminjamanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(248, 248, 248)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btnriwayatpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnriwayatpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel2.setText("ID PEMINJAMAN");

        jLabel3.setText("ID ANGGOTA");

        jLabel4.setText("ID BUKU");

        jLabel5.setText("TANGGAL");

        txtidanggota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtidanggotaActionPerformed(evt);
            }
        });

        jLabel6.setText("JUDUL");

        jLabel7.setText("JUMLAH");

        tblpeminjaman.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblpeminjaman);

        btnhapus.setText("Hapus");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        btnsimpan.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnsimpan.setText("Simpan");
        btnsimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsimpanActionPerformed(evt);
            }
        });

        jLabel8.setText("Total buku yang dipinjam");

        txttotalygdipinjam.setText("0");

        btntambah.setText("Tambah");
        btntambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });

        txtidbuku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtidbukuActionPerformed(evt);
            }
        });

        jLabel9.setText("//");

        jLabel10.setText("Nama");

        btnback.setText("Back");
        btnback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txttotalygdipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(318, 318, 318)
                        .addComponent(btnsimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 671, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel3))
                                    .addGap(30, 30, 30)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtidpeminjaman, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                                        .addComponent(txtidanggota))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel9)
                                    .addGap(12, 12, 12)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtnamaanggota, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtidbuku, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(28, 28, 28)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(txtjudulbuku, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel7)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtstokbukudipinjam, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                    .addGap(6, 6, 6))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txttanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 8, Short.MAX_VALUE))))))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnhapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btntambah, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                    .addComponent(btnback))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel5)
                                    .addComponent(txtidpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txttanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtidanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtnamaanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnback)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(txtjudulbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtstokbukudipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtidbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btntambah, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41)
                                .addComponent(btnhapus, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnsimpan, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txttotalygdipinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
        // TODO add your handling code here
        tambah();

    }//GEN-LAST:event_btntambahActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        txtidpeminjaman.setEditable(false);
        txttanggal.setEditable(false);
        txtjudulbuku.setEditable(false);
        txtnamaanggota.setEditable(false);
    }//GEN-LAST:event_formComponentShown

    private void btnsimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsimpanActionPerformed

        try {
            connection.setAutoCommit(false); // Nonaktifkan autocommit

            String sqlPeminjaman = "INSERT INTO peminjaman (id_peminjaman, id_anggota, jumlah_buku, tanggal_peminjaman) VALUES (?, ?, ?, ?)";
            String sqlDetail = "INSERT INTO detail_peminjaman (id_peminjaman, id_buku, judul_buku, jumlah_yang_dipinjam) VALUES (?, ?, ?, ?)";

            Set<String> idPeminjamanSet = new HashSet<>(); // Set untuk melacak id peminjaman yang sudah dimasukkan
            String idAnggota = ""; // Variable untuk menyimpan id anggota
            String tanggalPinjam = ""; // Variable untuk menyimpan tanggal pinjam

            // Pertama, masukkan data ke peminjaman
            for (int i = 0; i < tblpeminjaman.getRowCount(); i++) {
                String idPeminjaman = tblpeminjaman.getValueAt(i, 0).toString();
                idAnggota = (String) tblpeminjaman.getValueAt(i, 1).toString();
                tanggalPinjam = txttanggal.getText();

                // Hanya masukkan peminjaman jika idPeminjaman belum ada di set
                if (!idPeminjamanSet.contains(idPeminjaman)) {
                    try (PreparedStatement pstmtPeminjaman = connection.prepareStatement(sqlPeminjaman)) {
                        pstmtPeminjaman.setString(1, idPeminjaman);
                        pstmtPeminjaman.setString(2, idAnggota);
                        pstmtPeminjaman.setString(3, txttotalygdipinjam.getText());
                        pstmtPeminjaman.setDate(4, java.sql.Date.valueOf(tanggalPinjam));
                        pstmtPeminjaman.executeUpdate();
                        idPeminjamanSet.add(idPeminjaman); // Tambahkan ke set
                    }
                }
            }

            // Kedua, masukkan data ke detail peminjaman
            for (int i = 0; i < tblpeminjaman.getRowCount(); i++) {
                String idPeminjaman = tblpeminjaman.getValueAt(i, 0).toString();
                String idBuku = (String) tblpeminjaman.getValueAt(i, 3);
                String judulBuku = (String) tblpeminjaman.getValueAt(i, 4);
                int jumlahDipinjam = Integer.parseInt(tblpeminjaman.getValueAt(i, 5).toString());

                try (PreparedStatement pstmtDetail = connection.prepareStatement(sqlDetail)) {
                    pstmtDetail.setString(1, idPeminjaman);
                    pstmtDetail.setString(2, idBuku);
                    pstmtDetail.setString(3, judulBuku);
                    pstmtDetail.setInt(4, jumlahDipinjam);
                    pstmtDetail.executeUpdate();
                }
            }

            id_auto();
            connection.commit(); // Commit transaksi
            JOptionPane.showMessageDialog(null, "Peminjaman berhasil disimpan!");
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback jika terjadi kesalahan
                    JOptionPane.showMessageDialog(null, "Transaksi dibatalkan: " + ex.getMessage());
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            kosong();
            clearTextField();
            txtnamaanggota.setText("");
            txttotalygdipinjam.setText("");
        }
    }//GEN-LAST:event_btnsimpanActionPerformed

    private void txtidbukuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtidbukuActionPerformed
        // TODO add your handling code here:
        lookupBuku();
    }//GEN-LAST:event_txtidbukuActionPerformed

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
        // TODO add your handling code here:
        // DefaultTableModel model = (DefaultTableModel) tblpeminjaman.getModel();

        //model.removeRow(row);
        DefaultTableModel model = (DefaultTableModel) tblpeminjaman.getModel();
        int row = tblpeminjaman.getSelectedRow();
        model.removeRow(row);  // Menghapus baris pada indeks tertentu
        hitungTotalStok();
    }//GEN-LAST:event_btnhapusActionPerformed

    private void txtidanggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtidanggotaActionPerformed
        // TODO add your handling code here:
        lookupAnggota();
    }//GEN-LAST:event_txtidanggotaActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        // TODO add your handling code here:
        this.dispose();
        TampilanAwal frame = new TampilanAwal();
        frame.setVisible(true);
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnriwayatpeminjamanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnriwayatpeminjamanActionPerformed
        // TODO add your handling code here:
        this.dispose();
        RiwayatPeminjaman frame = new RiwayatPeminjaman();
        frame.setVisible(true);
    }//GEN-LAST:event_btnriwayatpeminjamanActionPerformed

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
            java.util.logging.Logger.getLogger(Peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Peminjaman.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Peminjaman().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btnriwayatpeminjaman;
    private javax.swing.JButton btnsimpan;
    private javax.swing.JButton btntambah;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblpeminjaman;
    private javax.swing.JTextField txtidanggota;
    private javax.swing.JTextField txtidbuku;
    private javax.swing.JTextField txtidpeminjaman;
    private javax.swing.JTextField txtjudulbuku;
    private javax.swing.JTextField txtnamaanggota;
    private javax.swing.JTextField txtstokbukudipinjam;
    private javax.swing.JTextField txttanggal;
    private javax.swing.JTextField txttotalygdipinjam;
    // End of variables declaration//GEN-END:variables
}
