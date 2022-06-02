package com.mobwal.android.library.data.zip;

public class ZipResult {
    private byte[] compress;
    private double k;
    private byte[] origin;

    public ZipResult(byte[] origin2) {
        this.origin = origin2;
    }

    public ZipResult getResult(byte[] compress2) {
        this.compress = compress2;
        this.k = (double) (compress2.length * 100) / this.origin.length;
        return this;
    }

    public byte[] getCompress() {
        return this.compress;
    }

    public double getK() {
        return this.k;
    }
}
