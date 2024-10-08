/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package dbl_uts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import javax.swing.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author M. HASNAN AL ABIYYU
 */
public final class Pengembalian extends javax.swing.JFrame implements PropertyChangeListener {

    private final Connection connection = ConnectDb.getConnectDb().getConnection();
    private String dateString = null;

    public Pengembalian() {
        initComponents();
        loadData(tblsemupemesanan);
        id_auto();
        tableModelPengembalian();
        datetglkembali.getDateEditor().addPropertyChangeListener(this);
        hitungTotalDenda();
    }
    
    private void hitungTotalDenda() {
        int totalDenda = 0;
        int jumlahBaris = tblpengembalian.getRowCount();  // Mengambil jumlah baris yang masih ada

        // Loop untuk menghitung stok dari setiap baris yang ada di JTable
        for (int i = 0; i < jumlahBaris; i++) {
            // Cek apakah kolom yang berisi stok memiliki nilai yang valid
            if (tblpengembalian.getValueAt(i, 6) != null) {
                try {
                    // Mengambil stok dari kolom yang sesuai (misal kolom ke-6)
                    int denda = Integer.parseInt(tblpengembalian.getValueAt(i, 6).toString());
                    totalDenda += denda;  // Menambahkan stok ke total
                } catch (NumberFormatException e) {
                    // Menangani kesalahan jika format angka salah
                    System.out.println("Format stok salah di baris: " + i);
                }
            }
        }

        // Menampilkan total stok pada komponen UI (misalnya TextField)
        txttotaldenda.setText("" + totalDenda);
    }
    
    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) tblpengembalian.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void hitungDenda(Date tanggalKembali) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalPinjamStr = txttanggalpinjam.getText();
            Date tanggalPinjam = sdf.parse(tanggalPinjamStr);

            long diffInMillies = Math.abs(tanggalKembali.getTime() - tanggalPinjam.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            // Hitung denda
            int denda = 0;
            if (diff > 7) {
                denda = (int) (diff - 7) * 5000; // Denda 5000 per hari setelah 7 hari
            }

            // Tampilkan denda di JTextField
            txtdenda.setText(String.valueOf(denda));

        } catch (ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Format tanggal salah!");
        }
    }

    public void loadData(JTable table) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        DefaultTableModel model = new DefaultTableModel();

        tblsemupemesanan.setModel(model);

        model.addColumn("Id Peminjaman");
        model.addColumn("Id Anggota");
        model.addColumn("tanggal pinjam");
        model.addColumn("id buku");
        model.addColumn("judul buku");
        model.addColumn("stok yang dipinjam");
        try {
            // Query SQL untuk mengambil data dari tabel pemesanan dan detail pemesanan
            String sql = "SELECT p.id_peminjaman, p.id_anggota, p.tanggal_peminjaman, dp.id_buku, dp.judul_buku, dp.jumlah_yang_dipinjam "
                    + "FROM peminjaman p "
                    + "JOIN detail_peminjaman dp ON p.id_peminjaman = dp.id_peminjaman ";

            // Siapkan statement
            stmt = connection.prepareStatement(sql);

            // Eksekusi query
            rs = stmt.executeQuery();

            // Model untuk JTable
            model.setRowCount(0);  // Hapus semua baris sebelum menambahkan data baru

            // Ambil data dari result set dan masukkan ke JTable
            while (rs.next()) {
                Object[] row = {
                    rs.getString("id_peminjaman"),
                    rs.getString("id_anggota"),
                    rs.getDate("tanggal_peminjaman"),
                    rs.getString("id_buku"),
                    rs.getString("judul_buku"),
                    rs.getInt("jumlah_yang_dipinjam")
                };
                model.addRow(row);  // Tambahkan baris ke model JTable
            }

        } catch (SQLException e) {
        } finally {
            // Tutup semua resource
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                // if (connection != null) connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public void tableModelPengembalian() {
        DefaultTableModel model = new DefaultTableModel();

        tblpengembalian.setModel(model);

        model.addColumn("Id Pengembalian");
        model.addColumn("Id Peminjaman");
        model.addColumn("Id Anggota");
        model.addColumn("id buku");
        model.addColumn("judul buku");
        model.addColumn("tanggal kembali");
        model.addColumn("denda");
        model.addColumn("stok kembali");
    }

    public void dataModelPengembalian() {
        if (dateString == null) {
            // Jika tanggal tidak dipilih, tampilkan pesan peringatan
            JOptionPane.showMessageDialog(null, "Tanggal pengembalian belum dipilih!");
            return;  // Hentikan eksekusi jika tanggal tidak dipilih
        }

        // Tambahkan nilai ke JTable
        DefaultTableModel model = (DefaultTableModel) tblpengembalian.getModel();
        model.addRow(new Object[]{
            txtidpengembalian.getText(),
            txtidpeminjaman.getText(),
            txtidanggota.getText(),
            txtidbuku.getText(),
            txtjudulbuku.getText(),
            dateString, // Tambahkan tanggal yang dipilih dalam format String
            txtdenda.getText(),
            txtstokkembali.getText()
        });

    }

    public void clearTextField() {
        txtidpeminjaman.setText("");
        txtidbuku.setText("");
        txtjudulbuku.setText("");
        txttanggalpinjam.setText("");
        txtdenda.setText("");
        txtstokkembali.setText("");
    }

    public final void id_auto() {
        try {
            final Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            final String sql = "select max (right(id_pengembalian, 1)) as no from pengembalian";
            final ResultSet rs = stat.executeQuery(sql);
            while (rs.next()) {
                if (rs.first() == false) {
                    txtidpengembalian.setText("PMB0001");
                } else {
                    rs.last();
                    int set_id = rs.getInt(1) + 1;
                    String no = String.valueOf(set_id);
                    int id_next = no.length();
                    for (int a = 0; a < 4 - id_next; a++) {
                        no = "0" + no;
                    }
                    txtidpengembalian.setText("PMB" + no);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Anggota.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cari() {
        String sql = "SELECT p.id_peminjaman, p.id_anggota, p.tanggal_peminjaman, dp.id_buku, b.judul_buku, dp.jumlah_yang_dipinjam "
                + "FROM peminjaman p "
                + "JOIN detail_peminjaman dp ON p.id_peminjaman = dp.id_peminjaman "
                + "JOIN buku b ON dp.id_buku = b.id_buku "
                + "WHERE p.id_peminjaman like '%" + txtcaripmsnn.getText() + "%'";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                // Buat model tabel untuk menampilkan hasil
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Id Peminjaman");
                model.addColumn("Id Anggota");
                model.addColumn("tanggal pinjam");
                model.addColumn("id buku");
                model.addColumn("judul buku");
                model.addColumn("stok yang dipinjam");

                // Loop melalui hasil ResultSet dan tambahkan ke model
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("id_peminjaman"),
                        rs.getString("id_anggota"),
                        rs.getDate("tanggal_peminjaman"),
                        rs.getString("id_buku"),
                        rs.getString("judul_buku"),
                        rs.getInt("jumlah_yang_dipinjam")
                    };
                    model.addRow(row);
                }

                // Set model tabel ke JTable
                tblsemupemesanan.setModel(model);
            }
        } catch (SQLException e) {
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
        btnriwayat = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtcaripmsnn = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblsemupemesanan = new javax.swing.JTable();
        btncaripmsnn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtidpengembalian = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtidanggota = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtidbuku = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtjudulbuku = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtidpeminjaman = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txttanggalpinjam = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtdenda = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        datetglkembali = new com.toedter.calendar.JDateChooser();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblpengembalian = new javax.swing.JTable();
        btntambah = new javax.swing.JButton();
        btnhapus = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txtstokkembali = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txttotaldenda = new javax.swing.JTextField();
        btnback = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("PENGEMBALIAN BUKU");

        btnriwayat.setText("Riwayat Pengembalian");
        btnriwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnriwayatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(320, 320, 320)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(btnriwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnriwayat, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jLabel2.setText("Masukkan ID Peminjaman");

        tblsemupemesanan.setModel(new javax.swing.table.DefaultTableModel(
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
        tblsemupemesanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblsemupemesananMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblsemupemesanan);

        btncaripmsnn.setText("Cari");
        btncaripmsnn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncaripmsnnActionPerformed(evt);
            }
        });

        jLabel3.setText("ID  Pengembalian");

        jLabel4.setText("ID Anggota");

        jLabel5.setText("ID Buku");

        jLabel6.setText("Judul Buku");

        jLabel7.setText("Tanggal Pinjam");

        jLabel8.setText("Tanggal Kembali");

        jLabel9.setText("Denda");

        jLabel10.setText("ID Peminjaman");

        datetglkembali.setDateFormatString("yyyy-MM-dd\n");

        tblpengembalian.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblpengembalian);

        btntambah.setText("Tambah");
        btntambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });

        btnhapus.setText("Hapus");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setText("SIMPAN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel11.setText("Stok kembali");

        jLabel13.setText("Total denda");

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
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 958, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtcaripmsnn, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btncaripmsnn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnback))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtidpengembalian)
                            .addComponent(txtidanggota)
                            .addComponent(txtidbuku)
                            .addComponent(txtjudulbuku, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addGap(90, 90, 90)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel8))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(datetglkembali, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(txtidpeminjaman, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txttanggalpinjam)
                            .addComponent(txtdenda, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtstokkembali, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13)
                            .addComponent(txttotaldenda))
                        .addGap(50, 50, 50)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnhapus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btntambah, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtcaripmsnn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btncaripmsnn)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(btnback)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtidpengembalian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtidpeminjaman, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(txtidanggota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7)
                                    .addComponent(txttanggalpinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtstokkembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtidbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(txtjudulbuku, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(datetglkembali, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtdenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txttotaldenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btntambah, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnhapus, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 207, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblsemupemesananMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblsemupemesananMouseClicked
        // TODO add your handling code here:
        int row = tblsemupemesanan.getSelectedRow();
        txtidpeminjaman.setText(tblsemupemesanan.getValueAt(row, 0).toString());
        txtidanggota.setText(tblsemupemesanan.getValueAt(row, 1).toString());
        txttanggalpinjam.setText(tblsemupemesanan.getValueAt(row, 2).toString());
        txtidbuku.setText(tblsemupemesanan.getValueAt(row, 3).toString());
        txtjudulbuku.setText(tblsemupemesanan.getValueAt(row, 4).toString());
        txtstokkembali.setText(tblsemupemesanan.getValueAt(row, 5).toString());
    }//GEN-LAST:event_tblsemupemesananMouseClicked

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // TODO add your handling code here:
        txtidpeminjaman.setEditable(false);
        txtidpengembalian.setEditable(false);
        txtidbuku.setEditable(false);
        txtidanggota.setEditable(false);
        txttanggalpinjam.setEditable(false);
        txtjudulbuku.setEditable(false);
        txttotaldenda.setEditable(false);
        txtdenda.setEditable(false);
    }//GEN-LAST:event_formComponentShown

    private void btncaripmsnnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncaripmsnnActionPerformed
        // TODO add your handling code here:
        this.cari();
    }//GEN-LAST:event_btncaripmsnnActionPerformed

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
        // TODO add your handling code here:
        dataModelPengembalian();
        clearTextField();
        hitungTotalDenda();
        //datetglkembali.setDate(null);
    }//GEN-LAST:event_btntambahActionPerformed

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblpengembalian.getModel();
        int row = tblpengembalian.getSelectedRow();
        model.removeRow(row);
        hitungTotalDenda();
    }//GEN-LAST:event_btnhapusActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
         try {
            connection.setAutoCommit(false); // Nonaktifkan autocommit

            String sqlPengembalian = "INSERT INTO pengembalian (id_pengembalian, id_anggota, tanggal_pengembalian, denda) VALUES (?, ?, ?, ?)";
            String sqlDetail = "INSERT INTO detail_pengembalian (id_pengembalian, id_buku, judul_buku, jumlah_yang_dikembalikan) VALUES (?, ?, ?, ?)";

            Set<String> idPengembalianSet = new HashSet<>(); // Set untuk melacak id peminjaman yang sudah dimasukkan
            String idAnggota = ""; // Variable untuk menyimpan id anggota

            int denda = Integer.parseInt(txttotaldenda.getText());
            // Pertama, masukkan data ke peminjaman
            for (int i = 0; i < tblpengembalian.getRowCount(); i++) {
                String idPengembalian = tblpengembalian.getValueAt(i, 0).toString();
                idAnggota = (String) tblpengembalian.getValueAt(i, 2).toString();
                String tanggalKembali = (String) tblpengembalian.getValueAt(i, 5).toString();
                
                
                // Hanya masukkan peminjaman jika idPeminjaman belum ada di set
                if (!idPengembalianSet.contains(idPengembalian)) {
                    try (PreparedStatement pstmtPeminjaman = connection.prepareStatement(sqlPengembalian)) {
                        pstmtPeminjaman.setString(1, idPengembalian);
                        pstmtPeminjaman.setString(2, idAnggota);
                        pstmtPeminjaman.setDate(3, java.sql.Date.valueOf(tanggalKembali));
                        pstmtPeminjaman.setInt(4, denda);
                        pstmtPeminjaman.executeUpdate();
                        idPengembalianSet.add(idPengembalian); // Tambahkan ke set
                    }
                }
            }

            // Kedua, masukkan data ke detail peminjaman
            for (int i = 0; i < tblpengembalian.getRowCount(); i++) {
                String idPengembalian = tblpengembalian.getValueAt(i, 0).toString();
                String idBuku = (String) tblpengembalian.getValueAt(i, 3);
                String judulBuku = (String) tblpengembalian.getValueAt(i, 4);
                int jumlahDikembalikan = Integer.parseInt(tblpengembalian.getValueAt(i, 7).toString());

                try (PreparedStatement pstmtDetail = connection.prepareStatement(sqlDetail)) {
                    pstmtDetail.setString(1, idPengembalian);
                    pstmtDetail.setString(2, idBuku);
                    pstmtDetail.setString(3, judulBuku);
                    pstmtDetail.setInt(4, jumlahDikembalikan);
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
        }
         kosong();
         txttotaldenda.setText("");
         txtidanggota.setText("");
         loadData(tblsemupemesanan);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbackActionPerformed
        // TODO add your handling code here:
        this.dispose();
        TampilanAwal frame = new TampilanAwal();
        frame.setVisible(true);
    }//GEN-LAST:event_btnbackActionPerformed

    private void btnriwayatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnriwayatActionPerformed
        // TODO add your handling code here:
        this.dispose();
        RiwayatPengembalian frame = new RiwayatPengembalian();
        frame.setVisible(true);
    }//GEN-LAST:event_btnriwayatActionPerformed

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
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Pengembalian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Pengembalian().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnback;
    private javax.swing.JButton btncaripmsnn;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btnriwayat;
    private javax.swing.JButton btntambah;
    private com.toedter.calendar.JDateChooser datetglkembali;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblpengembalian;
    private javax.swing.JTable tblsemupemesanan;
    private javax.swing.JTextField txtcaripmsnn;
    private javax.swing.JTextField txtdenda;
    private javax.swing.JTextField txtidanggota;
    private javax.swing.JTextField txtidbuku;
    private javax.swing.JTextField txtidpeminjaman;
    private javax.swing.JTextField txtidpengembalian;
    private javax.swing.JTextField txtjudulbuku;
    private javax.swing.JTextField txtstokkembali;
    private javax.swing.JTextField txttanggalpinjam;
    private javax.swing.JTextField txttotaldenda;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("date")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.dateString = dateFormat.format(evt.getNewValue());
            hitungDenda((Date) evt.getNewValue());
        }
    }
}
