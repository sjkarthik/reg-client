package io.mosip.registration.repositories;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.MachineMaster;

/**
 * The repository interface for {@link MachineMaster} entity
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
public interface MachineMasterRepository extends BaseRepository<MachineMaster, String>{
	
		
	/**
	 * Find machine based on  machine name.
	 * 
	 * @param machineName
	 * @return
	 */
	MachineMaster findByIsActiveTrueAndNameIgnoreCase(String machineName);

	MachineMaster findByNameIgnoreCase(String machineName);
	
}
