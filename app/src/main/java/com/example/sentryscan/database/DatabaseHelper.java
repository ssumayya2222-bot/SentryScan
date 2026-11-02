package com.example.sentryscan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sentryscan.model.CveEntry;
import com.example.sentryscan.model.OpenPort;
import com.example.sentryscan.model.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "sentryscan.db";
    private static final int DB_VERSION = 1;

    // the one and only object
    private static DatabaseHelper instance;

    // private constructor: nobody else can write new DatabaseHelper()
    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scans (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "target TEXT, date TEXT, risk_level TEXT)");
        db.execSQL("CREATE TABLE open_ports (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "scan_id INTEGER, port INTEGER, service TEXT, banner TEXT)");
        db.execSQL("CREATE TABLE cve_entries (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "cve_id TEXT, service TEXT, version TEXT, severity TEXT, description TEXT)");

        // a few sample CVE rows so the app has data from the start
        seed(db, "CVE-2011-2523", "vsftpd", "2.3.4", "Critical",
                "Backdoor in vsftpd 2.3.4 gives remote command access");
        seed(db, "CVE-2021-41773", "Apache", "2.4.49", "High",
                "Path traversal in Apache HTTP Server 2.4.49");
        seed(db, "CVE-2018-15473", "OpenSSH", "7.7", "Medium",
                "OpenSSH user enumeration through authentication timing");
        seed(db, "CVE-2017-0144", "SMB", "1", "Critical",
                "SMBv1 remote code execution (EternalBlue)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS scans");
        db.execSQL("DROP TABLE IF EXISTS open_ports");
        db.execSQL("DROP TABLE IF EXISTS cve_entries");
        onCreate(db);
    }

    private void seed(SQLiteDatabase db, String cveId, String service,
                      String version, String severity, String description) {
        ContentValues v = new ContentValues();
        v.put("cve_id", cveId);
        v.put("service", service);
        v.put("version", version);
        v.put("severity", severity);
        v.put("description", description);
        db.insert("cve_entries", null, v);
    }

    // ---------- scans ----------

    public long saveScan(ScanResult result) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues v = new ContentValues();
            v.put("target", result.getTarget());
            v.put("date", result.getDate());
            v.put("risk_level", result.getRiskLevel());
            long scanId = db.insert("scans", null, v);

            for (OpenPort p : result.getOpenPorts()) {
                ContentValues pv = new ContentValues();
                pv.put("scan_id", scanId);
                pv.put("port", p.getPort());
                pv.put("service", p.getService());
                pv.put("banner", p.getBanner());
                db.insert("open_ports", null, pv);
            }
            db.setTransactionSuccessful();
            result.setId((int) scanId);
            return scanId;
        } finally {
            db.endTransaction();
        }
    }

    public List<ScanResult> getAllScans() {
        List<ScanResult> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("scans", null, null, null, null, null, "id DESC")) {
            while (c.moveToNext()) {
                ScanResult r = new ScanResult(
                        c.getString(c.getColumnIndexOrThrow("target")),
                        c.getString(c.getColumnIndexOrThrow("date")));
                r.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                r.setRiskLevel(c.getString(c.getColumnIndexOrThrow("risk_level")));
                loadPorts(db, r);
                list.add(r);
            }
        }
        return list;
    }

    private void loadPorts(SQLiteDatabase db, ScanResult r) {
        try (Cursor c = db.query("open_ports", null, "scan_id = ?",
                new String[]{String.valueOf(r.getId())}, null, null, "port ASC")) {
            while (c.moveToNext()) {
                r.addOpenPort(new OpenPort(
                        c.getInt(c.getColumnIndexOrThrow("port")),
                        c.getString(c.getColumnIndexOrThrow("service")),
                        c.getString(c.getColumnIndexOrThrow("banner"))));
            }
        }
    }

    public void deleteScan(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {String.valueOf(id)};
        db.delete("open_ports", "scan_id = ?", args);
        db.delete("scans", "id = ?", args);
    }

    // ---------- CVE entries ----------

    public long insertCve(CveEntry e) {
        return getWritableDatabase().insert("cve_entries", null, toValues(e));
    }

    public int updateCve(CveEntry e) {
        return getWritableDatabase().update("cve_entries", toValues(e), "id = ?",
                new String[]{String.valueOf(e.getId())});
    }

    public int deleteCve(int id) {
        return getWritableDatabase().delete("cve_entries", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public List<CveEntry> getAllCves() {
        List<CveEntry> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("cve_entries", null, null, null, null, null, "id ASC")) {
            while (c.moveToNext()) {
                CveEntry e = new CveEntry(
                        c.getString(c.getColumnIndexOrThrow("cve_id")),
                        c.getString(c.getColumnIndexOrThrow("service")),
                        c.getString(c.getColumnIndexOrThrow("version")),
                        c.getString(c.getColumnIndexOrThrow("severity")),
                        c.getString(c.getColumnIndexOrThrow("description")));
                e.setId(c.getInt(c.getColumnIndexOrThrow("id")));
                list.add(e);
            }
        }
        return list;
    }

    private ContentValues toValues(CveEntry e) {
        ContentValues v = new ContentValues();
        v.put("cve_id", e.getCveId());
        v.put("service", e.getService());
        v.put("version", e.getVersion());
        v.put("severity", e.getSeverity());
        v.put("description", e.getDescription());
        return v;
    }
}