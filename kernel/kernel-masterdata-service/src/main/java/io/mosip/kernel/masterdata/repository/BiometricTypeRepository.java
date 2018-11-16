package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.BiometricType;

/**
 * @author Neha
 * @since 1.0.0
 *
 */

@Repository
public interface BiometricTypeRepository extends BaseRepository<BiometricType, String> {

	/**
	 * Get all Biometric types
	 *
	 * @return {@link List<BiometricType>}
	 */
	public List<BiometricType> findAllByIsActiveTrueAndIsDeletedFalse(Class<BiometricType> entityClass);

	/**
	 * Get all Biometric types of a specific language using language code
	 * 
	 * @param langCode
	 * @return {@link List<BiometricType>}
	 */
	List<BiometricType> findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(String langCode);

	/**
	 * Get Biometric type by specific id and language code
	 * 
	 * @param code
	 * @param langCode
	 * @return {@linkplain BiometricType}
	 */
	BiometricType findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(String code, String langCode);

}
