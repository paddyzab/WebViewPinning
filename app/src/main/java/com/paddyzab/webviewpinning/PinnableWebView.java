package com.paddyzab.webviewpinning;

import android.content.Context;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Extends Android WebView,
 * allowing user of this class to validate host certificate against provided one.
 *
 * Usually your DevOps team would provide you with the certificate used by your api, you will deliver
 * this certificate with application binary and test it PublicKeys of certificate of host against the
 * PubLicKey of provided one.
 * If the system is under MITM attack, the comparison will fail.
 */
public class PinnableWebView extends WebView {

    private CompromisedListener mCompromisedListener;

    public PinnableWebView(Context context) {
        this(context, null);
    }

    public PinnableWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PinnableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Configures the class, with the event listener on which the result will be delivered.
     * Allowing client class to prepare UI and messages for the user by implementing interface method.
     */
    public void setCompromisedListener(CompromisedListener compromisedListener) {
        mCompromisedListener = compromisedListener;
    }

    /**
     * Verifies that provided {@link X509Certificate} {@link PublicKey} matches the one extracted from the host.
     * @param certificate locally stored certificate, this will usually be delivered by your DevOps team.
     * @return
     */
    public boolean isCertificateValid(X509Certificate certificate) {
        final X509Certificate hostCertificate = generateX509Certificate(getCertificate());

        return hostCertificate != null && hostCertificate.getPublicKey().equals(certificate.getPublicKey());
    }

    @Nullable
    private X509Certificate generateX509Certificate(SslCertificate certificate) {
        final String bundleStorageKey = "x509-certificate";
        X509Certificate x509Certificate;

        final Bundle bundle = SslCertificate.saveState(certificate);
        final byte[] bytes = bundle != null ? bundle.getByteArray(bundleStorageKey) : null;

        if (bytes != null) {
            try {
                final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                final Certificate generatedCertificate = certificateFactory.generateCertificate(new ByteArrayInputStream(bytes));
                x509Certificate = (X509Certificate) generatedCertificate;
            } catch (CertificateException e) {
                if (mCompromisedListener != null) {
                    mCompromisedListener.handleCompromised();
                } else {
                    Log.e(PinnableWebView.class.getSimpleName(), "Listener to pass an event is null");
                }
                return null;
            }
        } else {
            return null;
        }

        return x509Certificate;
    }

    interface CompromisedListener {

        void handleCompromised();
    }
}
