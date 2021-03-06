// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.sdk.iot.provisioning.service.configs;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.microsoft.azure.sdk.iot.deps.serializer.ParserUtility;
import com.microsoft.azure.sdk.iot.provisioning.service.Tools;
import com.microsoft.azure.sdk.iot.provisioning.service.exceptions.ProvisioningServiceClientException;
import com.microsoft.azure.sdk.iot.provisioning.service.ProvisioningServiceClient;

import java.util.Date;

/**
 * Representation of a single Device Provisioning Service enrollment group with a JSON serializer and deserializer.
 *
 * <p> This object is used to send EnrollmentGroup information to the provisioning service, or receive EnrollmentGroup
 *     information from the provisioning service.
 *
 * <p> To create or update an EnrollmentGroup on the provisioning service you should fill this object and call the
 *     public API {@link ProvisioningServiceClient#createOrUpdateEnrollmentGroup(EnrollmentGroup)}.
 *     The minimum information required by the provisioning service is the {@link #enrollmentGroupId} and the
 *     {@link #attestation}.
 *
 * <p> To provision a device using EnrollmentGroup, it must contain a X509 chip with a signingCertificate for the
 *     {@link X509Attestation} mechanism.
 *
 * <p> The content of this class will be serialized in a JSON format and sent as a body of the rest API to the
 *     provisioning service.
 *
 * <p> When serialized, an EnrollmentGroup will look like the following example:
 * <pre>
 * {@code
 * {
 *     "enrollmentGroupId":"validEnrollmentGroupId",
 *     "attestation":{
 *         "type":"x509",
 *         "signingCertificates":{
 *             "primary":{
 *                 "certificate":"[valid certificate]"
 *             }
 *         }
 *     },
 *     "iotHubHostName":"ContosoIoTHub.azure-devices.net",
 *     "provisioningStatus":"enabled"
 * }
 * }
 * </pre>
 *
 * <p> The content of this class can be filled by a JSON, received from the provisioning service, as result of a
 *     EnrollmentGroup operation like create, update, or query EnrollmentGroup.
 *
 * <p> The following JSON is a sample of the EnrollmentGroup response, received from the provisioning service.
 * <pre>
 * {@code
 * {
 *     "enrollmentGroupId":"validEnrollmentGroupId",
 *     "attestation":{
 *         "type":"x509",
 *         "signingCertificates":{
 *             "primary":{
 *                 "certificate":"[valid certificate]",
 *                 "info": {
 *                     "subjectName": "CN=ROOT_00000000-0000-0000-0000-000000000000, OU=Azure IoT, O=MSFT, C=US",
 *                     "sha1Thumbprint": "0000000000000000000000000000000000",
 *                     "sha256Thumbprint": "validEnrollmentGroupId",
 *                     "issuerName": "CN=ROOT_00000000-0000-0000-0000-000000000000, OU=Azure IoT, O=MSFT, C=US",
 *                     "notBeforeUtc": "2017-11-14T12:34:18Z",
 *                     "notAfterUtc": "2017-11-20T12:34:18Z",
 *                     "serialNumber": "000000000000000000",
 *                     "version": 3
 *                 }
 *             }
 *         }
 *     },
 *     "iotHubHostName":"ContosoIoTHub.azure-devices.net",
 *     "provisioningStatus":"enabled",
 *     "createdDateTimeUtc": "2017-09-28T16:29:42.3447817Z",
 *     "lastUpdatedDateTimeUtc": "2017-09-28T16:29:42.3447817Z",
 *     "etag": "\"00000000-0000-0000-0000-00000000000\""
 * }
 * }
 * </pre>
 *
 * @see <a href="https://docs.microsoft.com/en-us/rest/api/iot-dps/deviceenrollmentgroup">Device Enrollment Group</a>
 */
public class EnrollmentGroup extends Serializable
{
    // the enrollment group identifier
    private static final String ENROLLMENT_GROUP_ID_TAG = "enrollmentGroupId";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(ENROLLMENT_GROUP_ID_TAG)
    private String enrollmentGroupId;

    // the attestation
    private static final String ATTESTATION_TAG = "attestation";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(ATTESTATION_TAG)
    private AttestationMechanism attestation;

    // the iothub host name
    private static final String IOTHUB_HOST_NAME_TAG = "iotHubHostName";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(IOTHUB_HOST_NAME_TAG)
    private String iotHubHostName;

    // the initial Twin state identifier (Twin is a special case and will be manually serialized).
    private static final String INITIAL_TWIN_STATE_TAG = "initialTwin";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(INITIAL_TWIN_STATE_TAG)
    private TwinState initialTwin;

    // the provisioning status
    private static final String PROVISIONING_STATUS_TAG = "provisioningStatus";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(PROVISIONING_STATUS_TAG)
    private ProvisioningStatus provisioningStatus;

    // the datetime this resource was created
    private static final String CREATED_DATETIME_UTC_TAG = "createdDateTimeUtc";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(CREATED_DATETIME_UTC_TAG)
    private String createdDateTimeUtc = null;
    @Expose(serialize = false, deserialize = false)
    private Date createdDateTimeUtcDate;

    // the datetime this resource was last updated
    private static final String LAST_UPDATED_DATETIME_UTC_TAG = "lastUpdatedDateTimeUtc";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(LAST_UPDATED_DATETIME_UTC_TAG)
    private String lastUpdatedDateTimeUtc = null;
    @Expose(serialize = false, deserialize = false)
    private Date lastUpdatedDateTimeUtcDate;

    // the eTag
    private static final String ETAG_TAG = "etag";
    @Expose(serialize = true, deserialize = true)
    @SerializedName(ETAG_TAG)
    private String etag;

    /**
     * CONSTRUCTOR
     *
     * <p> This constructor creates an instance of the enrollment group with
     *     the minimum set of information required by the provisioning service.
     *
     * <p> When serialized, an EnrollmentGroup will look like the following example:
     * <pre>
     * {@code
     * {
     *     "enrollmentGroupId":"validEnrollmentGroupId",
     *     "attestation":{
     *         "type":"x509",
     *         "signingCertificates":{
     *             "primary":{
     *                 "certificate":"[valid certificate]"
     *             }
     *         }
     *     }
     * }
     * }
     * </pre>
     *
     * @param enrollmentGroupId the {@code String} with an unique id for this enrollment group.
     * @param attestation the {@link Attestation} mechanism that shall be {@code signedCertificate} of {@link X509Attestation}.
     * @throws IllegalArgumentException If one of the provided parameters is not correct.
     */
    public EnrollmentGroup(
            String enrollmentGroupId,
            Attestation attestation)
    {
        /* SRS_ENROLLMENT_GROUP_21_001: [The constructor shall judge and store the provided parameters using the EnrollmentGroup setters.] */
        this.setEnrollmentGroupId(enrollmentGroupId);
        this.setAttestation(attestation);
    }

    /**
     * CONSTRUCTOR
     *
     * <p> This constructor creates an instance of the enrollment group filling
     *     the class with the information provided in the JSON.
     *
     * <p> The following JSON is a sample of the EnrollmentGroup response, received from the provisioning service.
     * <pre>
     * {@code
     * {
     *     "enrollmentGroupId":"validEnrollmentGroupId",
     *     "attestation":{
     *         "type":"x509",
     *         "signingCertificates":{
     *             "primary":{
     *                 "certificate":"[valid certificate]",
     *                 "info": {
     *                     "subjectName": "CN=ROOT_00000000-0000-0000-0000-000000000000, OU=Azure IoT, O=MSFT, C=US",
     *                     "sha1Thumbprint": "0000000000000000000000000000000000",
     *                     "sha256Thumbprint": "validEnrollmentGroupId",
     *                     "issuerName": "CN=ROOT_00000000-0000-0000-0000-000000000000, OU=Azure IoT, O=MSFT, C=US",
     *                     "notBeforeUtc": "2017-11-14T12:34:18Z",
     *                     "notAfterUtc": "2017-11-20T12:34:18Z",
     *                     "serialNumber": "000000000000000000",
     *                     "version": 3
     *                 }
     *             }
     *         }
     *     },
     *     "iotHubHostName":"ContosoIoTHub.azure-devices.net",
     *     "provisioningStatus":"enabled"
     *     "createdDateTimeUtc": "2017-09-28T16:29:42.3447817Z",
     *     "lastUpdatedDateTimeUtc": "2017-09-28T16:29:42.3447817Z",
     *     "etag": "\"00000000-0000-0000-0000-00000000000\""
     * }
     * }
     * </pre>
     *
     * @param json the {@code String} with the JSON received from the provisioning service.
     * @throws IllegalArgumentException If the provided JSON is null, empty, or invalid.
     */
    public EnrollmentGroup(String json)
    {
        /* SRS_ENROLLMENT_GROUP_21_002: [The constructor shall throw IllegalArgumentException if the JSON is null or empty.] */
        if(Tools.isNullOrEmpty(json))
        {
            throw new IllegalArgumentException("JSON with result is null or empty");
        }

        /* SRS_ENROLLMENT_GROUP_21_003: [The constructor shall throw JsonSyntaxException if the JSON is invalid.] */
        /* SRS_ENROLLMENT_GROUP_21_004: [The constructor shall deserialize the provided JSON for the enrollmentGroup class and subclasses.] */
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        EnrollmentGroup result = gson.fromJson(json, EnrollmentGroup.class);

        /* SRS_ENROLLMENT_GROUP_21_005: [The constructor shall judge and store the provided mandatory parameters `enrollmentGroupId` and `attestation` using the EnrollmentGroup setters.] */
        this.setEnrollmentGroupId(result.enrollmentGroupId);
        this.setAttestation(result.attestation);

        /* SRS_ENROLLMENT_GROUP_21_006: [If the `iotHubHostName`, `initialTwin`, or `provisioningStatus` is not null, the constructor shall judge and store it using the EnrollmentGroup setter.] */
        if(result.iotHubHostName != null)
        {
            this.setIotHubHostName(result.iotHubHostName);
        }
        if(result.provisioningStatus != null)
        {
            this.setProvisioningStatus(result.provisioningStatus);
        }
        if (result.initialTwin != null)
        {
            this.setInitialTwin(result.initialTwin);
        }

        /* SRS_ENROLLMENT_GROUP_21_007: [If the createdDateTimeUtc is not null, the constructor shall judge and store it using the EnrollmentGroup setter.] */
        if(result.createdDateTimeUtc != null)
        {
            this.setCreatedDateTimeUtc(result.createdDateTimeUtc);
        }

        /* SRS_ENROLLMENT_GROUP_21_008: [If the lastUpdatedDateTimeUtc is not null, the constructor shall judge and store it using the EnrollmentGroup setter.] */
        if (result.lastUpdatedDateTimeUtc != null)
        {
            this.setLastUpdatedDateTimeUtc(result.lastUpdatedDateTimeUtc);
        }

        /* SRS_ENROLLMENT_GROUP_21_009: [If the etag is not null, the constructor shall judge and store it using the EnrollmentGroup setter.] */
        if(result.etag != null)
        {
            this.setEtag(result.etag);
        }
    }

    /**
     * Serializer
     *
     * <p>
     *     Creates a {@code JsonElement}, which the content represents
     *     the information in this class and its subclasses in a JSON format.
     *
     *     This is useful if the caller will integrate this JSON with jsons from
     *     other classes to generate a consolidated JSON.
     * </p>

     * @return The {@code JsonElement} with the content of this class.
     */
    public JsonElement toJsonElement()
    {
        /* SRS_ENROLLMENT_GROUP_21_011: [The toJsonElement shall return a JsonElement with the information in this class in a JSON format.] */
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        JsonObject enrollmentGroupJson = gson.toJsonTree(this).getAsJsonObject();

        /* SRS_ENROLLMENT_GROUP_21_012: [If the initialTwin is not null, the toJsonElement shall include its content in the final JSON.] */
        if(initialTwin != null)
        {
            enrollmentGroupJson.add(INITIAL_TWIN_STATE_TAG, initialTwin.toJsonElement());
        }

        return enrollmentGroupJson;
    }

    /**
     * Getter for the enrollmentGroupId.
     *
     * @return The {@code String} with the enrollmentGroupId content. It cannot be {@code null} or empty.
     */
    public String getEnrollmentGroupId()
    {
        /* SRS_ENROLLMENT_GROUP_21_014: [The getEnrollmentGroupId shall return a String with the stored enrollmentGroupId.] */
        return this.enrollmentGroupId;
    }

    /**
     * Setter for the enrollmentGroupId.
     *
     * <p>
     *     A valid enrollment group Id shall follow this criteria.
     *         A case-sensitive string (up to 128 char long)
     *         of ASCII 7-bit alphanumeric chars
     *         + {'-', ':', '.', '+', '%', '_', '#', '*', '?', '!', '(', ')', ',', '=', '@', ';', '$', '''}.
     * </p>
     *
     * @param enrollmentGroupId the {@code String} with the new enrollmentGroupId. It cannot be {@code null}, empty, or invalid.
     * @throws IllegalArgumentException If the provided enrollmentGroupId is {@code null}, empty, or invalid.
     */
    protected void setEnrollmentGroupId(String enrollmentGroupId)
    {
        /* SRS_ENROLLMENT_GROUP_21_015: [The setEnrollmentGroupId shall throw IllegalArgumentException if the provided enrollmentGroupId is null, empty, or invalid.] */
        ParserUtility.validateId(enrollmentGroupId);

        /* SRS_ENROLLMENT_GROUP_21_016: [The setEnrollmentGroupId shall store the provided enrollmentGroupId.] */
        this.enrollmentGroupId = enrollmentGroupId;
    }

    /**
     * Getter for the attestation mechanism.
     *
     * @return The {@code Attestation} with the attestation content. It cannot be {@code null}.
     * @throws ProvisioningServiceClientException If the type of the attestation mechanism is unknown.
     */
    public Attestation getAttestation() throws ProvisioningServiceClientException
    {
        /* SRS_ENROLLMENT_GROUP_21_017: [The getAttestation shall return a Attestation with the stored attestation.] */
        return this.attestation.getAttestation();
    }

    /**
     * Setter for the attestation.
     *
     * <p>
     *     Attestation mechanism is mandatory parameter that provides the mechanism
     *     type and the necessary keys/certificates
     *
     *     @see AttestationMechanism
     * </p>
     *
     * @param attestationMechanism the {@code AttestationMechanism} with the new attestation mechanism. It can be `tpm` or `x509`.
     * @throws IllegalArgumentException If the provided attestation mechanism is {@code null}.
     */
    protected void setAttestation(AttestationMechanism attestationMechanism)
    {
        /* SRS_ENROLLMENT_GROUP_21_018: [The setAttestation shall throw IllegalArgumentException if the attestation is null.] */
        if(attestationMechanism == null)
        {
            throw new IllegalArgumentException("attestationMechanism cannot be null");
        }

        /* SRS_ENROLLMENT_GROUP_21_042: [The setAttestation shall throw IllegalArgumentException if the attestation is not X509 signingCertificate.] */
        /* SRS_ENROLLMENT_GROUP_21_019: [The setAttestation shall store the provided attestation.] */
        try
        {
            this.setAttestation(attestationMechanism.getAttestation());
        }
        catch (ProvisioningServiceClientException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Setter for the attestation.
     *
     * <p> Attestation mechanism is mandatory parameter that provides the mechanism
     *     type and the necessary certificates.
     *
     * <p> EnrollmentGroup only accept {@link X509Attestation} with the RootCertificates. You can create it
     *     providing the <b>.pem</b> content to {@link X509Attestation#createFromRootCertificates(String, String)}
     *
     * @see Attestation
     * @see X509Attestation
     *
     * @param attestation the {@link Attestation} with the new attestation mechanism. It shall be {@link X509Attestation}.
     * @throws IllegalArgumentException If the provided attestation mechanism is {@code null} or invalid.
     */
    public void setAttestation(Attestation attestation)
    {
        /* SRS_ENROLLMENT_GROUP_21_039: [The setAttestation shall throw IllegalArgumentException if the attestation is null.] */
        if(attestation == null)
        {
            throw new IllegalArgumentException("attestation cannot be null");
        }

        /* SRS_ENROLLMENT_GROUP_21_040: [The setAttestation shall throw IllegalArgumentException if the attestation is not X509 signingCertificate.] */
        if(!(attestation instanceof X509Attestation))
        {
            throw new IllegalArgumentException("attestation for EnrollmentGroup shall be X509");
        }
        if(((X509Attestation)attestation).getRootCertificates() == null)
        {
            throw new IllegalArgumentException("attestation for EnrollmentGroup shall be X509 signingCertificate");
        }

        /* SRS_ENROLLMENT_GROUP_21_041: [The setAttestation shall store the provided attestation using the AttestationMechanism object.] */
        this.attestation = new AttestationMechanism(attestation);
    }

    /**
     * Getter for the iotHubHostName.
     *
     * @return The {@code String} with the iotHubHostName content. It cannot be {@code null} or empty.
     */
    public String getIotHubHostName()
    {
        /* SRS_ENROLLMENT_GROUP_21_020: [The getIotHubHostName shall return a String with the stored iotHubHostName.] */
        return this.iotHubHostName;
    }

    /**
     * Setter for the iotHubHostName.
     *
     * <p>
     *     A valid iothub host name shall follow this criteria.
     *         A case-sensitive string (up to 128 char long)
     *         of ASCII 7-bit alphanumeric chars
     *         + {'-', ':', '.', '+', '%', '_', '#', '*', '?', '!', '(', ')', ',', '=', '@', ';', '$', '''}.
     *     A valid host name shall have, at least 2 parts separated by '.'.
     * </p>
     *
     * @param iotHubHostName the {@code String} with the new iotHubHostName. It cannot be {@code null}, empty, or invalid.
     * @throws IllegalArgumentException If the provided iotHubHostName is {@code null}, empty, or invalid.
     */
    public void setIotHubHostName(String iotHubHostName)
    {
        /* SRS_ENROLLMENT_GROUP_21_021: [The setIotHubHostName shall throw IllegalArgumentException if the iotHubHostName is null, empty, or invalid.] */
        ParserUtility.validateHostName(iotHubHostName);

        /* SRS_ENROLLMENT_GROUP_21_022: [The setIotHubHostName shall store the provided iotHubHostName.] */
        this.iotHubHostName = iotHubHostName;
    }

    /**
     * Getter for the initialTwin.
     *
     * @return The {@code TwinState} with the initialTwin content. Its optional and can be {@code null}.
     */
    public TwinState getInitialTwin()
    {
        /* SRS_ENROLLMENT_GROUP_21_023: [The getInitialTwin shall return a TwinState with the stored initialTwin.] */
        return this.initialTwin;
    }

    /**
     * Setter for the initialTwin.
     *
     * <p>
     *     It provides a Twin precondition for the provisioned device.
     * </p>
     *
     * @param initialTwin the {@code TwinState} with the new initialTwin. It cannot be {@code null}.
     * @throws IllegalArgumentException If the provided initialTwin is {@code null}.
     */
    public void setInitialTwin(TwinState initialTwin)
    {
        /* SRS_ENROLLMENT_GROUP_21_024: [The setInitialTwin shall throw IllegalArgumentException if the initialTwin is null.] */
        if(initialTwin == null)
        {
            throw new IllegalArgumentException("initialTwin cannot be null");
        }

        /* SRS_ENROLLMENT_GROUP_21_025: [The setInitialTwin shall store the provided initialTwin.] */
        this.initialTwin = initialTwin;
    }

    /**
     * Getter for the provisioningStatus.
     *
     * @return The {@code ProvisioningStatus} with the provisioningStatus content. It can be 'enabled' or 'disabled'.
     */
    public ProvisioningStatus getProvisioningStatus()
    {
        /* SRS_ENROLLMENT_GROUP_21_026: [The getProvisioningStatus shall return a TwinState with the stored provisioningStatus.] */
        return this.provisioningStatus;
    }

    /**
     * Setter for the provisioningStatus.
     *
     * <p>
     *     It provides a Status precondition for the provisioned device.
     * </p>
     *
     * @param provisioningStatus the {@code ProvisioningStatus} with the new provisioningStatus. It cannot be {@code null}.
     * @throws IllegalArgumentException If the provided provisioningStatus is {@code null}.
     */
    public void setProvisioningStatus(ProvisioningStatus provisioningStatus)
    {
        /* SRS_ENROLLMENT_GROUP_21_027: [The setProvisioningStatus shall throw IllegalArgumentException if the provisioningStatus is null.] */
        if(provisioningStatus == null)
        {
            throw new IllegalArgumentException("provisioningStatus cannot be null");
        }

        /* SRS_ENROLLMENT_GROUP_21_028: [The setProvisioningStatus shall store the provided provisioningStatus.] */
        this.provisioningStatus = provisioningStatus;
    }

    /**
     * Getter for the createdDateTimeUtcDate.
     *
     * @return The {@code Date} with the createdDateTimeUtcDate content. It can be {@code null}.
     */
    public Date getCreatedDateTimeUtc()
    {
        /* SRS_ENROLLMENT_GROUP_21_029: [The getCreatedDateTimeUtc shall return a Date with the stored createdDateTimeUtcDate.] */
        return this.createdDateTimeUtcDate;
    }

    /**
     * Setter for the createdDateTimeUtc.
     *
     * <p>
     *     This Date and Time is provided by the provisioning service. If the enrollmentGroup is not created yet,
     *     this string can represent an invalid Date. In this case, it will be ignored.
     *
     *     Example of the expected format:
     *         "2016-06-01T21:22:43.7996883Z"
     * </p>
     *
     * @param createdDateTimeUtc the {@code String} with the new createdDateTimeUtc. It can be {@code null}, empty or not valid.
     */
    protected void setCreatedDateTimeUtc(String createdDateTimeUtc)
    {
        /* SRS_ENROLLMENT_GROUP_21_030: [The setCreatedDateTimeUtc shall parse the provided String as a Data and Time UTC.] */
        /* SRS_ENROLLMENT_GROUP_21_031: [The setCreatedDateTimeUtc shall throw IllegalArgumentException if it cannot parse the provided createdDateTimeUtc] */
        this.createdDateTimeUtcDate = ParserUtility.getDateTimeUtc(createdDateTimeUtc);
    }

    /**
     * Getter for the lastUpdatedDateTimeUtcDate.
     *
     * @return The {@code Date} with the lastUpdatedDateTimeUtcDate content. It can be {@code null}.
     */
    public Date getLastUpdatedDateTimeUtc()
    {
        /* SRS_ENROLLMENT_GROUP_21_032: [The getLastUpdatedDateTimeUtc shall return a Date with the stored lastUpdatedDateTimeUtcDate.] */
        return this.lastUpdatedDateTimeUtcDate;
    }

    /**
     * Setter for the lastUpdatedDateTimeUtc.
     *
     * <p>
     *     This Date and Time is provided by the provisioning service. If the enrollmentGroup is not created yet,
     *     this string can represent an invalid Date. In this case, it will be ignored.
     *
     *     Example of the expected format:
     *         "2016-06-01T21:22:43.7996883Z"
     * </p>
     *
     * @param lastUpdatedDateTimeUtc the {@code String} with the new lastUpdatedDateTimeUtc. It can be {@code null}, empty or not valid.
     */
    protected void setLastUpdatedDateTimeUtc(String lastUpdatedDateTimeUtc)
    {
        /* SRS_ENROLLMENT_GROUP_21_033: [The setLastUpdatedDateTimeUtc shall parse the provided String as a Data and Time UTC.] */
        /* SRS_ENROLLMENT_GROUP_21_034: [The setLastUpdatedDateTimeUtc shall throw IllegalArgumentException if it cannot parse the provided lastUpdatedDateTimeUtc] */
        this.lastUpdatedDateTimeUtcDate = ParserUtility.getDateTimeUtc(lastUpdatedDateTimeUtc);
    }

    /**
     * Getter for the etag.
     *
     * @return The {@code String} with the etag content. It can be {@code null}.
     */
    public String getEtag()
    {
        /* SRS_ENROLLMENT_GROUP_21_035: [The getEtag shall return a String with the stored etag.] */
        return this.etag;
    }

    /**
     * Setter for the etag.
     *
     * @param etag the {@code String} with the new etag. It cannot be {@code null}, empty or invalid.
     * @throws IllegalArgumentException If the provided etag is {@code null}, empty or invalid.
     */
    protected void setEtag(String etag)
    {
        /* SRS_ENROLLMENT_GROUP_21_036: [The setEtag shall throw IllegalArgumentException if the etag is null, empty, or invalid.] */
        ParserUtility.validateStringUTF8(etag);

        /* SRS_ENROLLMENT_GROUP_21_037: [The setEtag shall store the provided etag.] */
        this.etag = etag;
    }

    /**
     * Empty constructor
     *
     * <p>
     *     Used only by the tools that will deserialize this class.
     * </p>
     */
    @SuppressWarnings("unused")
    protected EnrollmentGroup()
    {
        /* SRS_ENROLLMENT_GROUP_21_038: [The EnrollmentGroup shall provide an empty constructor to make GSON happy.] */
    }
}
