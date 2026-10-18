package com.mycompany.mavenproject2;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Kelas utama (Main Class) JavaFX yang menyediakan Antarmuka Pengguna (GUI)
 * untuk Sistem Pegadaian Emas. Menangani navigasi tabel nasabah, pendaftaran, dan pengelolaan transaksi gadai.
 */
public class App extends Application {

    private Pegadaian pegadaian = new Pegadaian();
    
    private ObservableList<Nasabah> dbNasabahList = FXCollections.observableArrayList(); 
    private ObservableList<TransaksiGadai> dbTransaksiList = FXCollections.observableArrayList();

    private TableView<Nasabah> tableNasabah;
    private TextField txtSearchUtama;
    private DatePicker dpSimulasi = new DatePicker(LocalDate.now()); 

    private NumberFormat rpFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private DateTimeFormatter dtfCetak = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    /**
     * Entry point metode GUI JavaFX. Membangun halaman beranda daftar nasabah.
     * 
     * @param primaryStage Stage utama yang menampung komponen antarmuka.
     */
    @Override
    public void start(Stage primaryStage) {
        
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #15803d;"); 
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("Sistem Pegadaian Emas");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblSimulasi = new Label("Tanggal Sistem (Simulasi):");
        lblSimulasi.setTextFill(Color.WHITE);
        lblSimulasi.setStyle("-fx-font-weight: bold;");
        dpSimulasi.setPrefWidth(120);

        txtSearchUtama = new TextField();
        txtSearchUtama.setPromptText("🔍 Cari NIK atau Nama...");
        txtSearchUtama.setPrefWidth(220);
        txtSearchUtama.setStyle("-fx-background-radius: 20; -fx-padding: 5 15;");

        Button btnAddUser = new Button("+ Tambah User Baru");
        btnAddUser.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        btnAddUser.setOnAction(e -> openFormTambahUser(primaryStage));

        topBar.getChildren().addAll(lblTitle, spacer, lblSimulasi, dpSimulasi, txtSearchUtama, btnAddUser);

        tableNasabah = new TableView<>();
        tableNasabah.setPlaceholder(new Label("Belum ada data nasabah. Silakan klik '+ Tambah User Baru'."));
        tableNasabah.setStyle("-fx-font-size: 14px;");

        TableColumn<Nasabah, String> colNik = new TableColumn<>("NIK");
        colNik.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNik()));
        colNik.setPrefWidth(150);

        TableColumn<Nasabah, String> colNama = new TableColumn<>("Nama Nasabah");
        colNama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
        colNama.setPrefWidth(250);

        TableColumn<Nasabah, String> colHp = new TableColumn<>("No HP");
        colHp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNoHp()));
        colHp.setPrefWidth(150);

        TableColumn<Nasabah, Void> colAksi = new TableColumn<>("Aksi");
        colAksi.setPrefWidth(150);
        colAksi.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Lihat Transaksi ➔");
            {
                btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    Nasabah nasabahTerpilih = getTableView().getItems().get(getIndex());
                    openDetailTransaksiWindow(primaryStage, nasabahTerpilih);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    HBox box = new HBox(btn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tableNasabah.getColumns().addAll(colNik, colNama, colHp, colAksi);
        
        FilteredList<Nasabah> filteredData = new FilteredList<>(dbNasabahList, p -> true);
        txtSearchUtama.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(nasabah -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lower = newValue.toLowerCase();
                return nasabah.getNik().toLowerCase().contains(lower) || nasabah.getNama().toLowerCase().contains(lower);
            });
        });
        SortedList<Nasabah> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableNasabah.comparatorProperty());
        tableNasabah.setItems(sortedData);

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(20));
        centerBox.setStyle("-fx-background-color: #f3f4f6;");
        Label lblDaftar = new Label("Daftar Nasabah Terdaftar");
        lblDaftar.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        VBox.setVgrow(tableNasabah, Priority.ALWAYS);
        centerBox.getChildren().addAll(lblDaftar, tableNasabah);

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(centerBox); 

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Sistem Pegadaian Emas GUI (JavaFX)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Membuka form modal pendaftaran nasabah baru.
     * 
     * @param parentStage Stage induk yang mengatur fokus/modality jendela.
     */
    private void openFormTambahUser(Stage parentStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); 
        dialog.initOwner(parentStage);
        dialog.setTitle("Pendaftaran User Baru");

        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        formPanel.setStyle("-fx-background-color: #f9fafb;");

        Label lblErrorMsg = new Label("");
        lblErrorMsg.setTextFill(Color.RED);
        lblErrorMsg.setVisible(false);

        TextField txtNik = new TextField(); txtNik.setPromptText("Masukkan NIK");
        TextField txtNama = new TextField(); txtNama.setPromptText("Nama Lengkap KTP");
        TextField txtNoHp = new TextField(); txtNoHp.setPromptText("No HP / WhatsApp");

        GridPane grid = new GridPane();
        grid.setVgap(15); grid.setHgap(10);
        grid.add(new Label("Data Nasabah"), 0, 0, 2, 1);
        grid.add(new Label("NIK:"), 0, 1); grid.add(txtNik, 1, 1);
        grid.add(new Label("Nama:"), 0, 2); grid.add(txtNama, 1, 2);
        grid.add(new Label("No HP:"), 0, 3); grid.add(txtNoHp, 1, 3);

        Button btnSimpan = new Button("Simpan User");
        btnSimpan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        
        btnSimpan.setOnAction(e -> {
            String nik = txtNik.getText().trim();
            if(nik.isEmpty() || txtNama.getText().isEmpty()) {
                lblErrorMsg.setText("Error: NIK dan Nama wajib diisi!");
                lblErrorMsg.setVisible(true);
                return;
            }
            boolean isExist = dbNasabahList.stream().anyMatch(n -> n.getNik().equals(nik));
            if (isExist) {
                lblErrorMsg.setText("Error: NIK sudah terdaftar di sistem!");
                lblErrorMsg.setVisible(true);
                return;
            }
            Nasabah nasabahBaru = new Nasabah(nik, txtNama.getText().toUpperCase(), txtNoHp.getText());
            dbNasabahList.add(nasabahBaru);
            dialog.close();
        });

        formPanel.getChildren().addAll(lblErrorMsg, grid, btnSimpan);
        dialog.setScene(new Scene(formPanel, 350, 280));
        dialog.showAndWait();
    }

    /**
     * Membuka jendela tampilan Detail Transaksi per spesifik nasabah.
     * Menyediakan list transaksi dan form pratinjau bukti nota gadai.
     * 
     * @param parentStage Stage utama aplikasi.
     * @param nasabah Entitas nasabah yang dipilih untuk melihat detailnya.
     */
    private void openDetailTransaksiWindow(Stage parentStage, Nasabah nasabah) {
        Stage detailStage = new Stage();
        detailStage.initOwner(parentStage);
        detailStage.setTitle("Detail Transaksi - " + nasabah.getNama());

        FilteredList<TransaksiGadai> userTrxList = new FilteredList<>(dbTransaksiList, trx -> trx.getNasabah().getNik().equals(nasabah.getNik()));

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #e5e7eb;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblDetail = new Label("Riwayat Transaksi: " + nasabah.getNama() + " (" + nasabah.getNik() + ")");
        lblDetail.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnTambahTrx = new Button("+ Buat Transaksi Gadai Baru");
        btnTambahTrx.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnTambahTrx.setOnAction(e -> openFormTambahTransaksi(detailStage, nasabah, userTrxList));
        topBar.getChildren().addAll(lblDetail, spacer, btnTambahTrx);

        TableView<TransaksiGadai> tableTrx = new TableView<>(userTrxList);
        tableTrx.setPlaceholder(new Label("Belum ada riwayat gadai untuk nasabah ini."));
        
        TableColumn<TransaksiGadai, String> colId = new TableColumn<>("ID TRX");
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdTransaksi()));
        
        TableColumn<TransaksiGadai, String> colTgl = new TableColumn<>("Tanggal");
        colTgl.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTanggalTransaksi().format(df)));
        
        TableColumn<TransaksiGadai, String> colPinjaman = new TableColumn<>("Plafon Cair");
        colPinjaman.setCellValueFactory(cellData -> new SimpleStringProperty(rpFormat.format(cellData.getValue().getNominalPinjaman())));
        
        TableColumn<TransaksiGadai, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(cellData -> {
            TransaksiGadai trx = cellData.getValue();
            if (trx.isLunas()) return new SimpleStringProperty("LUNAS");
            if (trx.isMenunggak(dpSimulasi.getValue())) return new SimpleStringProperty("MENUNGGAK");
            return new SimpleStringProperty("AKTIF");
        });

        tableTrx.getColumns().addAll(colId, colTgl, colPinjaman, colStatus);
        
        dpSimulasi.valueProperty().addListener((obs, oldVal, newVal) -> tableTrx.refresh());

        VBox centerBox = new VBox(tableTrx);
        centerBox.setPadding(new Insets(10));
        VBox.setVgrow(tableTrx, Priority.ALWAYS);

        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle("-fx-background-color: #f8fafc;"); 
        rightPanel.setAlignment(Pos.TOP_CENTER);

        VBox notaContainer = new VBox(10);
        notaContainer.setPadding(new Insets(20));
        notaContainer.setStyle("-fx-background-color: white; -fx-border-color: #16a34a; -fx-border-width: 4 0 0 0; -fx-background-radius: 0 0 5 5;");
        notaContainer.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.1)));
        notaContainer.setVisible(false); 

        Label titleNota = new Label("PEGADAIAN DIGITAL\nBukti Transaksi Gadai");
        titleNota.setStyle("-fx-font-weight: bold; -fx-text-alignment: center;");
        titleNota.setAlignment(Pos.CENTER);
        
        Label stampLunas = new Label("LUNAS");
        stampLunas.setStyle("-fx-text-fill: red; -fx-border-color: red; -fx-border-width: 3; -fx-font-size: 30px; -fx-font-weight: bold; -fx-padding: 5;");
        stampLunas.setRotate(-15);
        stampLunas.setVisible(false);
        stampLunas.setTranslateY(150);
        stampLunas.setTranslateX(50);
        
        Label outTgl = new Label("-"), outId = new Label("-"), outKode = new Label("-"), outBerat = new Label("-");
        Label outKarat = new Label("-"), outTaksiran = new Label("-"), outPinjaman = new Label("-");
        Label outTenor = new Label("-"), outBunga = new Label("-"), outJatuhTempo = new Label("-"), outCicilan = new Label("-");
        Label outTelahBayar = new Label("-"), outSisaKali = new Label("-"), outSisaTagihan = new Label("-"), outTglCetak = new Label("-");
        outTgl.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        notaContainer.getChildren().addAll(
            new StackPane(titleNota, stampLunas), outTgl, new Separator(),
            createRow("No. TRX:", outId), new Separator(),
            new Label("DATA JAMINAN"), createRow("Kode:", outKode), createRow("Karat:", outKarat), createRow("Berat:", outBerat),
            new Separator(),
            new Label("RINCIAN KEUANGAN"), createRow("Taksiran:", outTaksiran), createRow("Plafon (85%):", outPinjaman),
            createRow("Tenor:", outTenor), createRow("Bunga/Bln:", outBunga), createRow("Jatuh Tempo:", outJatuhTempo),
            new Separator(), createRow("CICILAN / BLN:", outCicilan), new Separator()
        );

        VBox statusBox = new VBox(5);
        statusBox.setPadding(new Insets(10));
        statusBox.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 5;");
        statusBox.getChildren().addAll(
            createRow("Telah Dibayar:", outTelahBayar), createRow("Sisa Angsuran:", outSisaKali),
            new Separator(), createRow("SISA TAGIHAN:", outSisaTagihan)
        );
        outTglCetak.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280; -fx-padding: 10 0 0 0;");
        notaContainer.getChildren().addAll(statusBox, outTglCetak);

        Button btnBayar = new Button("Bayar Angsuran / Pelunasan");
        btnBayar.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
        btnBayar.setMaxWidth(Double.MAX_VALUE);
        btnBayar.setVisible(false);

        rightPanel.getChildren().addAll(new Label("Struk Transaksi Terpilih"), notaContainer, btnBayar);

        ScrollPane scrollRight = new ScrollPane(rightPanel);
        scrollRight.setFitToWidth(true);
        scrollRight.setPrefWidth(350);

        tableTrx.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                notaContainer.setVisible(true);
                btnBayar.setVisible(true);
                
                outId.setText(newSel.getIdTransaksi());
                outTgl.setText(newSel.getTanggalTransaksi().format(df));
                outKode.setText(newSel.getEmas().getKodeBarang());
                outKarat.setText(newSel.getEmas().getKarat() + " Karat");
                outBerat.setText(newSel.getEmas().getBerat() + " gr");
                outTaksiran.setText(rpFormat.format(newSel.getHargaAwal()));
                outPinjaman.setText(rpFormat.format(newSel.getNominalPinjaman()));
                outTenor.setText(newSel.getLamaAngsuran() + " Bulan");
                outBunga.setText(newSel.getPersentaseBunga() + "%");
                
                LocalDate jatuhTempoNext = newSel.getTanggalTransaksi().plusMonths(newSel.getBulanPembayaranKe());
                outJatuhTempo.setText(jatuhTempoNext.format(df));
                
                outCicilan.setText(rpFormat.format(newSel.getCicilanPerBulan()));
                outTelahBayar.setText(newSel.getTelahDibayar() + "x");
                outSisaKali.setText(newSel.getSisaAngsuran() + "x");
                outSisaTagihan.setText(rpFormat.format(newSel.getSisaTagihan()));
                outTglCetak.setText("Dicetak: " + LocalDateTime.now().format(dtfCetak));

                if (newSel.isLunas()) {
                    stampLunas.setVisible(true);
                    btnBayar.setDisable(true);
                    btnBayar.setText("Transaksi Lunas");
                    btnBayar.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
                } else {
                    stampLunas.setVisible(false);
                    btnBayar.setDisable(false);
                    
                    if (newSel.isMenunggak(dpSimulasi.getValue())) {
                        btnBayar.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
                        btnBayar.setText("BAYAR TUNGGAKAN & DENDA!");
                    } else {
                        btnBayar.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
                        btnBayar.setText("Bayar Angsuran / Pelunasan");
                    }
                }
            }
        });

        btnBayar.setOnAction(e -> {
            TransaksiGadai trx = tableTrx.getSelectionModel().getSelectedItem();
            if (trx == null || trx.isLunas()) return;

            Alert dialogBayar = new Alert(Alert.AlertType.CONFIRMATION);
            dialogBayar.setTitle("Pembayaran");
            dialogBayar.setHeaderText("Pembayaran TRX: " + trx.getIdTransaksi());
            ButtonType btn1Bulan = new ButtonType("Bayar 1 Bulan");
            ButtonType btnLunas = new ButtonType("Pelunasan Total");
            ButtonType btnBatal = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialogBayar.getButtonTypes().setAll(btn1Bulan, btnLunas, btnBatal);

            Optional<ButtonType> result = dialogBayar.showAndWait();
            if (result.isPresent() && result.get() != btnBatal) {
                if (result.get() == btn1Bulan) trx.bayarAngsuran();
                else if (result.get() == btnLunas) {
                    int sisa = trx.getSisaAngsuran();
                    for (int i = 0; i < sisa; i++) trx.bayarAngsuran();
                }
                tableTrx.refresh();
                
                int selectedIdx = tableTrx.getSelectionModel().getSelectedIndex();
                tableTrx.getSelectionModel().clearSelection();
                tableTrx.getSelectionModel().select(selectedIdx);
            }
        });

        BorderPane rootDetail = new BorderPane();
        rootDetail.setTop(topBar);
        rootDetail.setCenter(centerBox);
        rootDetail.setRight(scrollRight);

        Scene sceneDetail = new Scene(rootDetail, 950, 600);
        detailStage.setScene(sceneDetail);
        detailStage.show();
    }

    /**
     * Membuka form pendaftaran kontrak transaksi gadai baru (hanya terbuka dari Detail Nasabah).
     * 
     * @param parentStage Jendela detail transaksi nasabah.
     * @param nasabah Nasabah yang bersangkutan.
     * @param userTrxList List transaksi milik nasabah untuk mengecek tunggakan.
     */
    private void openFormTambahTransaksi(Stage parentStage, Nasabah nasabah, FilteredList<TransaksiGadai> userTrxList) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL); 
        dialog.initOwner(parentStage);
        dialog.setTitle("Buat Transaksi Gadai");

        VBox formPanel = new VBox(15);
        formPanel.setPadding(new Insets(20));
        
        Label lblErrorMsg = new Label("");
        lblErrorMsg.setTextFill(Color.RED);
        lblErrorMsg.setWrapText(true);
        lblErrorMsg.setVisible(false);

        TextField txtKodeEmas = new TextField(); txtKodeEmas.setPromptText("Misal: LM01");
        TextField txtBerat = new TextField(); txtBerat.setPromptText("Berat (Gram)");
        TextField txtKarat = new TextField(); txtKarat.setPromptText("Karat (Misal: 24)");
        ComboBox<Integer> cbTenor = new ComboBox<>();
        cbTenor.getItems().addAll(3, 6, 12);
        cbTenor.setValue(3);

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(10);
        grid.add(new Label("Data Jaminan Emas"), 0, 0, 2, 1);
        grid.add(new Label("Kode Brg:"), 0, 1); grid.add(txtKodeEmas, 1, 1);
        grid.add(new Label("Berat (g):"), 0, 2); grid.add(txtBerat, 1, 2);
        grid.add(new Label("Karat:"), 0, 3); grid.add(txtKarat, 1, 3);
        grid.add(new Label("Tenor (Bulan):"), 0, 4); grid.add(cbTenor, 1, 4);

        Button btnSimpan = new Button("Simpan Transaksi");
        btnSimpan.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSimpan.setMaxWidth(Double.MAX_VALUE);
        
        btnSimpan.setOnAction(e -> {
            try {
                boolean menunggak = userTrxList.stream()
                        .anyMatch(trx -> trx.isMenunggak(dpSimulasi.getValue()));
                
                if (menunggak) {
                    lblErrorMsg.setText("DITOLAK: Nasabah ini terdeteksi memiliki cicilan yang MENUNGGAK (Telat Bayar). Harap lunasi tunggakan terlebih dahulu!");
                    lblErrorMsg.setVisible(true);
                    return; 
                }

                Emas emas = new Emas(txtKodeEmas.getText().toUpperCase(), Double.parseDouble(txtBerat.getText()), Integer.parseInt(txtKarat.getText()));
                TransaksiGadai trx = pegadaian.terimaGadai(nasabah, emas, cbTenor.getValue());
                
                dbTransaksiList.add(trx); 
                dialog.close();
                
            } catch (NumberFormatException ex) {
                lblErrorMsg.setText("Error: Pastikan input berat dan karat berupa angka!");
                lblErrorMsg.setVisible(true);
            }
        });

        formPanel.getChildren().addAll(lblErrorMsg, grid, btnSimpan);
        dialog.setScene(new Scene(formPanel, 350, 320));
        dialog.showAndWait();
    }

    /**
     * Membantu pembuatan row label dan text dinamis untuk layout struk nota secara rapi.
     * 
     * @param labelText Tulisan di sebelah kiri row.
     * @param valueLabel Label node target untuk penempatan value di sebelah kanan row.
     * @return HBox yang membungkus komponen label dengan susunan flexbox.
     */
    private HBox createRow(String labelText, Label valueLabel) {
        HBox box = new HBox();
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-text-fill: #4b5563;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.getChildren().addAll(lbl, spacer, valueLabel);
        return box;
    }

    /**
     * Metode utama pembuka program Java.
     * 
     * @param args Parameter eksekusi argumen shell (bila ada).
     */
    public static void main(String[] args) {
        launch(args);
    }
}