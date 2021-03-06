/*
 *
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 *
 */

package com.microsoft.azure.sdk.iot.provisioning.security.hsm;

import com.microsoft.azure.sdk.iot.provisioning.security.SecurityProviderX509;
import com.microsoft.azure.sdk.iot.provisioning.security.exceptions.SecurityClientException;
import com.microsoft.msr.RiotEmulator.RIoT;

import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.LinkedList;

public class SecurityProviderDiceEmulator extends SecurityProviderX509
{
    private final String commonNameAlias;
    private final String commonNameSigner;
    private final String commonNameRoot;

    // read this data from DICE HW after boot
    private static final byte[] FWID = {
                                            0x11, 0x12, 0x13, 0x14, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
                                        };
    private static final byte[] SEED = {
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08
                                        };

    private RIoT.DeviceAuthBundle diceBundle;

    /**
     * Constructor to build the DICE certs from the simulator
     */
    public SecurityProviderDiceEmulator()
    {
        //SRS_SecurityClientDiceEmulator_25_001: [ Constructor shall create a default unique names for Alias Certificate, Signer Certificate and Root certificate ]
        this.commonNameAlias = "microsoftriotcore";
        this.commonNameSigner = "microsoftriotcoresigner";
        this.commonNameRoot = "microsoftriotcoreroot";

        //SRS_SecurityClientDiceEmulator_25_002: [ Constructor shall create a diceBundle by calling CreateDeviceAuthBundle ]
        this.diceBundle = RIoT.CreateDeviceAuthBundle(
                SEED,
                FWID,
                false,
                commonNameRoot, commonNameSigner, commonNameAlias);
    }

    /**
     * Constructor to build DICE certs from the simulator
     * @param commonNameAlias A string value for the common name of alias cert. Cannot be {@code null} or empty
     * @param commonNameSigner A string value for the common name of signer cert. Cannot be {@code null} or empty
     * @param commonNameRoot A string value for the common name of root cert. cannot be {@code null} or empty
     * @throws SecurityClientException This exception is thrown if any of the input values are invalid
     */
    public SecurityProviderDiceEmulator(String commonNameAlias, String commonNameSigner, String commonNameRoot) throws SecurityClientException
    {
        //SRS_SecurityClientDiceEmulator_25_003: [ Constructor shall throw SecurityClientException if Alias Certificate, Signer Certificate and Root certificate names are null or empty ]
        if (commonNameAlias == null || commonNameAlias.isEmpty())
        {
            throw new SecurityClientException(new IllegalArgumentException("commonNameAlias cannot be null or empty"));
        }

        if (commonNameSigner == null || commonNameSigner.isEmpty())
        {
            throw new SecurityClientException(new IllegalArgumentException("commonNameSigner cannot be null or empty"));
        }

        if (commonNameRoot == null || commonNameRoot.isEmpty())
        {
            throw new SecurityClientException(new IllegalArgumentException("commonNameRoot cannot be null or empty"));
        }

        //SRS_SecurityClientDiceEmulator_25_004: [ Constructor shall throw SecurityClientException if Alias Certificate, Signer Certificate and Root certificate names are not unique ]
        if (commonNameAlias.equals(commonNameSigner) || commonNameSigner.equals(commonNameRoot) || commonNameAlias.equals(commonNameRoot))
        {
            throw new SecurityClientException(new IllegalArgumentException("Use unique names for common name"));
        }

        this.commonNameAlias = commonNameAlias;
        this.commonNameSigner = commonNameSigner;
        this.commonNameRoot = commonNameRoot;

        this.diceBundle = RIoT.CreateDeviceAuthBundle(
                SEED,
                FWID,
                false,
                this.commonNameRoot, this.commonNameSigner, this.commonNameAlias);
    }

    /**
     * Getter for the common name
     * @return The common name for the root cert
     */
    @Override
    public String getClientCertificateCommonName()
    {
        //SRS_SecurityClientDiceEmulator_25_005: [ This method shall return Root certificate name as common name ]
        return commonNameAlias;
    }

    /**
     * Getter for the Alias certificate
     * @return Alias certificate
     */
    @Override
    public X509Certificate getClientCertificate()
    {
        //SRS_SecurityClientDiceEmulator_25_006: [ This method shall return Alias certificate generated by DICE ]
        return this.diceBundle.AliasCert;
    }

    /**
     * Getter for Alias key
     * @return Alias private key
     */
    @Override
    public Key getClientPrivateKey()
    {
        //SRS_SecurityClientDiceEmulator_25_007: [ This method shall return Alias private key generated by DICE ]
        return this.diceBundle.AliasPrivateKey;
    }

    @Override
    public Collection<X509Certificate> getIntermediateCertificatesChain()
    {
        Collection<X509Certificate> certificateCollection = new LinkedList<>();
        certificateCollection.add(this.diceBundle.DeviceIDCert);
        return certificateCollection;
    }


    /**
     * Getter for the Alias cert in PEM format
     * @return Alias cert in PEM format
     */
    public String getAliasCertPem()
    {
        //SRS_SecurityClientDiceEmulator_25_009: [ This method shall return Alias certificate generated by DICE as PEM string]
        return this.diceBundle.AliasCertPem;
    }

    /**
     * Getter for the Alias cert in PEM format
     * @return Alias cert in PEM format
     */
    public String getAliasCertPrivateKeyPem()
    {
        //SRS_SecurityClientDiceEmulator_25_009: [ This method shall return Alias certificate generated by DICE as PEM string]
        return this.diceBundle.AliasPrivateKeyPem;
    }

    /**
     * Getter for the Signer cert in PEM format
     * @return Signer cert in PEM format
     */
    public String getSignerCertPem()
    {
        //SRS_SecurityClientDiceEmulator_25_010: [ This method shall return Signer certificate generated by DICE as PEM string ]
        return this.diceBundle.DeviceIDCertPem;
    }

    /**
     * Getter for the Root cert in PEM format
     * @return Root cert in PEM format
     */
    public String getRootCertPem()
    {
        //SRS_SecurityClientDiceEmulator_25_011: [ This method shall return Root certificate generated by DICE as PEM string ]
        return this.diceBundle.RootCertPem;
    }

    /**
     * Generates leaf certificate with the unique id as common name
     * @param uniqueId Unique ID to be used in common name. Cannot be {@code null} or empty
     * @return A PEM formatted leaf cert with unique ID as common name
     */
    public String generateLeafCert(String uniqueId) throws SecurityClientException
    {
        //SRS_SecurityClientDiceEmulator_25_012: [ This method shall throw SecurityClientException if unique id is null or empty ]
        if (uniqueId == null || uniqueId.isEmpty())
        {
            throw new SecurityClientException(new IllegalArgumentException("unique id cannot be null or empty"));
        }
        //SRS_SecurityClientDiceEmulator_25_012: [ This method shall return Leaf certificate generated by DICE with unique ID as common Name in PEM Format ]
        RIoT.CreateLeafCert(this.diceBundle, uniqueId);

        RIoT.DeviceAuthBundle diceBundleVerifier = RIoT.CreateDeviceAuthBundle(
                SEED,
                FWID,
                false,
                this.commonNameRoot, uniqueId, this.commonNameAlias);

        return this.diceBundle.LeafCertPem;
    }
}
