/*
 * Copyright (c) 2011-2012 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE file for licensing information.
 */
package eu.emi.security.authn.x509.impl;

import java.io.IOException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.emi.security.authn.x509.StoreUpdateListener;
import eu.emi.security.authn.x509.X509CertChainValidator;
import eu.emi.security.authn.x509.helpers.crl.CRLParameters;
import eu.emi.security.authn.x509.helpers.pkipath.PlainCRLValidator;
import eu.emi.security.authn.x509.helpers.trust.DirectoryTrustAnchorStore;

/**
 * The certificate validator which uses a flexible set of certificates and CRL locations.
 * Both CA certificates or CRLs can be provided as a list of locations. Each element 
 * in the list is either a URL to a concrete file (note that this might be remote file)
 * or a local path. In the latter case it is possible to use wildcards in path locations.
 * <p>
 * It is possible to configure this validator to refresh both CRL and CA 
 * certificate locations on a regular interval.
 * <p>
 * Note: be very careful when using remote CA certificate locations. If such a remote 
 * location is compromised or DNS address is spooffed then your system is also compromised.  
 * <p>
 * The CRLs (Certificate Revocation Lists, if their handling is turned on) can be obtained
 * also from the CA certificate extension defining CRL URL if are not provided explicitly.
 * 
 * @author K. Benedyczak
 * @see X509CertChainValidator
 */
public class DirectoryCertChainValidator extends PlainCRLValidator
{
	private DirectoryTrustAnchorStore trustStore;
	
	/**
	 * Constructs a new validator instance. CRLs (Certificate Revocation Lists) 
	 * are taken from the trusted CAs certificate extension and downloaded, 
	 * unless CRL checking is disabled. Additional CRLs may be provided manually.  
	 * 
	 * @param trustedLocations trusted certificates locations, either as local wildcard
	 * paths or URLs
	 * @param crlParams CRL settings
	 * @param crlMode defines overall CRL handling mode
	 * @param connectionTimeoutCA connection timeout in ms for downloading remote CA certificates
	 * @param diskCache directory path, where the remote CA certificates shall be cached 
	 * after downloading. Can be null if cache shall not be used.
	 * @param allowProxy whether the validator should allow for Proxy certificates
	 * @param listeners initial list of update listeners. If set in the constructor 
	 * then even the initial problems will be reported (if set via appropriate methods 
	 * then only error of subsequent updates are reported). 
	 * @throws IOException 
	 * @throws KeyStoreException 
	 */
	public DirectoryCertChainValidator(List<String> trustedLocations, 
			CRLParameters crlParams, CrlCheckingMode crlMode, 
			long truststoreUpdateInterval, int connectionTimeoutCA, 
			String diskCache, boolean allowProxy, 
			Collection<? extends StoreUpdateListener> listeners) 
					throws KeyStoreException, IOException 
	{
		super(crlParams, crlMode, listeners);
		trustStore = new DirectoryTrustAnchorStore(trustedLocations, diskCache, 
				connectionTimeoutCA, timer, truststoreUpdateInterval, listeners);
		init(trustStore, crlStoreImpl, allowProxy, crlMode);
	}
	
	/**
	 * Constructs a new validator instance. CRLs (Certificate Revocation Lists) 
	 * are taken from the trusted CAs certificate extension and downloaded, 
	 * unless CRL checking is disabled. Additional CRLs may be provided manually.  
	 * 
	 * @param trustedLocations trusted certificates locations, either as local wildcard
	 * paths or URLs
	 * @param crlParams CRL settings
	 * @param crlMode defines overall CRL handling mode
	 * @param connectionTimeoutCA connection timeout in ms for downloading remote CA certificates
	 * @param diskCache directory path, where the remote CA certificates shall be cached 
	 * after downloading. Can be null if cache shall not be used.
	 * @param allowProxy whether the validator should allow for Proxy certificates
	 * @throws IOException 
	 * @throws KeyStoreException 
	 */
	public DirectoryCertChainValidator(List<String> trustedLocations, 
			CRLParameters crlParams, CrlCheckingMode crlMode, 
			long truststoreUpdateInterval, int connectionTimeoutCA, 
			String diskCache, boolean allowProxy) 
					throws KeyStoreException, IOException 
	{
		this(trustedLocations, crlParams, crlMode, truststoreUpdateInterval, 
				connectionTimeoutCA, diskCache, allowProxy, 
				new ArrayList<StoreUpdateListener>(0));
	}
	
	/**
	 * Returns the interval between subsequent checks of the truststore files. 
	 * @return the current refresh interval in milliseconds
	 */
	public long getTruststoreUpdateInterval()
	{
		return trustStore.getUpdateInterval();
	}

	/**
	 * Sets a new interval between subsequent checks of the truststore
	 * files. 
	 * @param updateInterval the new interval to be set in milliseconds
	 */
	public void setTruststoreUpdateInterval(long updateInterval)
	{
		trustStore.setUpdateInterval(updateInterval);
	}

	/**
	 * Returns the current truststore locations
	 * @return the path
	 */
	public List<String> getTruststorePaths()
	{
		return trustStore.getLocations();
	}
	
	/**
	 * Sets new trusted locations. See constructor argument description
	 * for details.
	 */
	public void setTruststorePaths(List<String> trustedLocations)
	{
		trustStore.dispose();
		trustStore = new DirectoryTrustAnchorStore(trustedLocations, 
				trustStore.getCacheDir(), trustStore.getConnTimeout(), 
				timer, trustStore.getUpdateInterval(), observers);
		init(trustStore, null, isProxyAllowed(), getCrlCheckingMode());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose()
	{
		super.dispose();
		trustStore.dispose();
	}
}






