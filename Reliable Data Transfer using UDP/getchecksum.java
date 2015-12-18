/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package fcnprojfile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.System.in;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * Class to get the checksum of data
 * @author shraddha
 */
public class getchecksum {

    public Long computechecksum(String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        CheckedInputStream cisobj = new CheckedInputStream(file, new CRC32());
        BufferedInputStream in = new BufferedInputStream(cisobj);
        while (in.read() != -1) {
            //reading the file contents
        }
        return cisobj.getChecksum().getValue();

    }

    public static long datachecksum(byte[] data) {
        Checksum chksum = new CRC32();
        chksum.update(data, 0, data.length);
        return chksum.getValue();
    }
}
