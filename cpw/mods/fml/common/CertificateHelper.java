// 
// Decompiled by Procyon v0.6.0
// 

package cpw.mods.fml.common;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.cert.Certificate;

public class CertificateHelper
{
    private static final String HEXES = "0123456789abcdef";
    
    public static String getFingerprint(final Certificate certificate) {
        if (certificate == null) {
            return "NO VALID CERTIFICATE FOUND";
        }
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] der = certificate.getEncoded();
            md.update(der);
            final byte[] digest = md.digest();
            return hexify(digest);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    public static String getFingerprint(final ByteBuffer buffer) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(buffer);
            final byte[] chksum = digest.digest();
            return hexify(chksum);
        }
        catch (final Exception e) {
            return null;
        }
    }
    
    private static String hexify(final byte[] chksum) {
        final StringBuilder hex = new StringBuilder(2 * chksum.length);
        for (final byte b : chksum) {
            hex.append("0123456789abcdef".charAt((b & 0xF0) >> 4)).append("0123456789abcdef".charAt(b & 0xF));
        }
        return hex.toString();
    }
}
