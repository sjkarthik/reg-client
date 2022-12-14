package io.mosip.registration.service.sync.impl;

import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import io.micrometer.core.annotation.Timed;
import io.mosip.kernel.clientcrypto.service.impl.ClientCryptoFacade;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.ConnectionException;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.LoggerConstants;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.tpm.PublicKeyUploadRequestDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
import io.mosip.registration.service.sync.TPMPublicKeySyncService;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

/**
 * Service implementation class to sync the TPM Public Key with the server.
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Service
public class TPMPublicKeySyncServiceImpl  extends BaseService implements TPMPublicKeySyncService {

	private static final Logger LOGGER = AppConfig.getLogger(TPMPublicKeySyncServiceImpl.class);
	
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	@Autowired
	private ClientCryptoFacade clientCryptoFacade;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.service.sync.TPMPublicKeySyncService#syncTPMPublicKey()
	 */
	@Timed
	@Override
	@SuppressWarnings("unchecked")
	public ResponseDTO syncTPMPublicKey() throws RegBaseCheckedException {

		LOGGER.info("Verify TPM Public Key mapping in server started");

		try {
			// Get the Public Key of the TPM Signing Key
			RequestWrapper<PublicKeyUploadRequestDTO> tpmKeyUploadRequest = new RequestWrapper<>();
			tpmKeyUploadRequest.setId(
					String.valueOf(ApplicationContext.map().get(RegistrationConstants.REGISTRATION_CLIENT)));
			tpmKeyUploadRequest.setVersion(RegistrationConstants.VER);
			tpmKeyUploadRequest.setRequesttime(DateUtils.getUTCCurrentDateTime());
			PublicKeyUploadRequestDTO publicKeyUploadRequestDTO = new PublicKeyUploadRequestDTO();
			publicKeyUploadRequestDTO.setMachineName(RegistrationSystemPropertiesChecker.getMachineId());
			publicKeyUploadRequestDTO.setPublicKey(CryptoUtil.encodeToURLSafeBase64(clientCryptoFacade.getClientSecurity()
					.getEncryptionPublicPart()));
			publicKeyUploadRequestDTO.setSignPublicKey(CryptoUtil.encodeToURLSafeBase64(clientCryptoFacade.getClientSecurity()
					.getSigningPublicPart()));
			tpmKeyUploadRequest.setRequest(publicKeyUploadRequestDTO);

			Map<String, Object> publicKeyResponse = (Map<String, Object>) serviceDelegateUtil.post(
					RegistrationConstants.TPM_PUBLIC_KEY_SYNC_SERVICE_NAME, tpmKeyUploadRequest,
					RegistrationConstants.JOB_TRIGGER_POINT_USER);

			if (null != publicKeyResponse.get(RegistrationConstants.RESPONSE)) {
				// Add the Key Index and Machine Name to Application Context
				return setSuccessResponse(new ResponseDTO(), RegistrationConstants.MACHINE_VERIFICATION_SUCCESS, null);

			} else {
				if (null != publicKeyResponse.get(RegistrationConstants.ERRORS)) {
					LOGGER.error(LoggerConstants.TPM_PUBLIC_KEY_UPLOAD, APPLICATION_NAME, APPLICATION_ID,
							"Error while verifying the TPM public key mappings");

					((List<Map<String, String>>) publicKeyResponse.get(RegistrationConstants.ERRORS))
							.forEach(error -> LOGGER.error(LoggerConstants.TPM_PUBLIC_KEY_UPLOAD, APPLICATION_NAME,
									APPLICATION_ID,
									String.format("Error Code: %s\t Error Message: %s",
											error.get(RegistrationConstants.ERROR_CODE),
											error.get(RegistrationConstants.MESSAGE_CODE))));
				}
				throw new RegBaseCheckedException(RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorCode(),
						RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorMessage());
			}
		} catch (ConnectionException | RegBaseCheckedException checkedException) {
			throw new RegBaseCheckedException(RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorCode(),
					RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorMessage(), checkedException);
		} catch (RuntimeException tpmPublicKeySyncRuntimeExp) {
			throw new RegBaseUncheckedException(RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorCode(),
					RegistrationExceptionConstants.TPM_PUBLIC_KEY_UPLOAD.getErrorMessage(), tpmPublicKeySyncRuntimeExp);
		} finally {
			LOGGER.info(LoggerConstants.TPM_PUBLIC_KEY_UPLOAD, APPLICATION_NAME, APPLICATION_ID,
					"Completed sync of TPM Public Key");
		}
	}

}
