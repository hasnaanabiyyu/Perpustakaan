Create Table anggota(
	id_anggota VARCHAR(32) NOT NULL PRIMARY KEY,
	nama VARCHAR (32) NOT NULL,
	alamat VARCHAR(32) NOT NULL
);

CREATE TABLE buku(
	id_buku VARCHAR(32) NOT NULL PRIMARY KEY,
	judul_buku VARCHAR(32) NOT NULL UNIQUE,
	penerbit VARCHAR(32) NOT NULL
);
Alter Table buku
ADD Column stok varchar(32);

CREATE TABLE peminjaman(
	id_peminjaman VARCHAR(32) NOT NULL PRIMARY KEY,
	id_anggota VARCHAR(32) NOT NULL,
	jumlah_buku VARCHAR(32) NOT NULL,
	tanggal_peminjaman DATE NOT NULL,
	
	CONSTRAINT fk_anggota
	FOREIGN KEY (id_anggota) REFERENCES anggota (id_anggota)
);
 
CREATE TABLE detail_peminjaman(
	id_peminjaman VARCHAR(32) NOT NULL,
	id_buku VARCHAR(32) NOT NULL,
	judul_buku VARCHAR(32) NOT NULL,
	
	CONSTRAINT fk_peminjaman 
	FOREIGN KEY (id_peminjaman) REFERENCES peminjaman (id_peminjaman),
	
	CONSTRAINT fk_buku 
	FOREIGN KEY (id_buku) REFERENCES buku (id_buku),
	
	CONSTRAINT fk_judul_buku 
	FOREIGN KEY (judul_buku) REFERENCES buku (judul_buku)
	
);

ALTER TABLE detail_peminjaman
ADD CONSTRAINT detail_peminjaman_pkey PRIMARY KEY (id_peminjaman, id_buku)
SELECT * FROM detail_peminjaman

CREATE TABLE pengembalian(
	id_pengembalian VARCHAR (32) NOT NULL PRIMARY KEY,
	id_anggota VARCHAR(32) NOT NULL,
	id_peminjaman VARCHAR(32) NOT NULL,
	tanggal_pengembalian DATE,
	denda VARCHAR (32),
	
	CONSTRAINT fk_peminjaman 
	FOREIGN KEY (id_peminjaman) REFERENCES peminjaman (id_peminjaman),
	
	CONSTRAINT fk_anggota 
	FOREIGN KEY (id_anggota) REFERENCES anggota (id_anggota)
);

ALTER TABLE pengembalian
DROP COLUMN id_peminjaman;
ALTER COLUMN denda
SET DATA TYPE INTEGER USING denda::INTEGER;

CREATE TABLE detail_pengembalian(
id_pengembalian VARCHAR(32) NOT NULL,
	id_buku VARCHAR(32) NOT NULL,
	judul_buku VARCHAR(32) NOT NULL,
	jumlah_yang_dikembalikan VARCHAR(32),
	
	CONSTRAINT fk_pengembalian
	FOREIGN KEY (id_pengembalian) REFERENCES pengembalian (id_pengembalian),
	
	CONSTRAINT fk_buku 
	FOREIGN KEY (id_buku) REFERENCES buku (id_buku),
	
	CONSTRAINT fk_judul_buku 
	FOREIGN KEY (judul_buku) REFERENCES buku (judul_buku)
)


ALTER TABLE detail_pengembalian
ADD CONSTRAINT detail_pengembalian_pkey PRIMARY KEY (id_pengembalian, id_buku)
SELECT * FROM detail_peminjaman

Alter Table pengembalian
Alter column denda Drop not null 

CREATE OR REPLACE FUNCTION kurangi_stok_buku() 
RETURNS TRIGGER AS $$
BEGIN
    -- Mengurangi stok buku sesuai jumlah yang dipinjam
    UPDATE buku 
    SET stok = stok - NEW.jumlah_yang_dipinjam
    WHERE id_buku = NEW.id_buku;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger
CREATE TRIGGER after_insert_detail_peminjaman
AFTER INSERT ON detail_peminjaman
FOR EACH ROW
EXECUTE FUNCTION kurangi_stok_buku();

ALTER TABLE buku
ALTER COLUMN stok
SET DATA TYPE INTEGER USING stok::INTEGER;

ALTER TABLE detail_peminjaman
ALTER COLUMN jumlah_yang_dipinjam
SET DATA TYPE INTEGER USING jumlah_yang_dipinjam::INTEGER;

ALTER TABLE detail_pengembalian
ALTER COLUMN jumlah_yang_dikembalikan
SET DATA TYPE INTEGER USING jumlah_yang_dikembalikan::INTEGER;

CREATE OR REPLACE FUNCTION tambah_stok_buku()
RETURNS TRIGGER AS $$
DECLARE
    jumlah_dipinjam INT;
BEGIN
    -- Mengambil jumlah yang dipinjam dari detail_peminjaman
    SELECT jumlah_yang_dipinjam 
    INTO jumlah_dipinjam 
    FROM detail_peminjaman
    WHERE id_buku = NEW.id_buku;
    
    -- Mengecek apakah jumlah yang dikembalikan melebihi yang dipinjam
    IF NEW.jumlah_yang_dikembalikan > jumlah_dipinjam THEN
        RAISE EXCEPTION 'Jumlah buku yang dikembalikan (%s) melebihi jumlah yang dipinjam (%s)', 
            NEW.jumlah_yang_dikembalikan, jumlah_dipinjam;
        -- Rollback otomatis akan terjadi karena ada EXCEPTION
    END IF;

    -- Menambahkan stok buku di tabel buku
    UPDATE buku
    SET stok = stok + NEW.jumlah_yang_dikembalikan
    WHERE id_buku = NEW.id_buku;

    -- Mengurangi jumlah yang dipinjam di detail_peminjaman
    UPDATE detail_peminjaman
    SET jumlah_yang_dipinjam = jumlah_yang_dipinjam - NEW.jumlah_yang_dikembalikan
    WHERE id_buku = NEW.id_buku;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER after_insert_detail_pengembalian
AFTER INSERT ON detail_pengembalian
FOR EACH ROW
EXECUTE FUNCTION tambah_stok_buku();

