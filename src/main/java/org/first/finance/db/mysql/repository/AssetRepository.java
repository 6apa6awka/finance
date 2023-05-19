package org.first.finance.db.mysql.repository;

import org.first.finance.db.mysql.entity.Asset;
import org.springframework.data.repository.CrudRepository;

public interface AssetRepository extends CrudRepository<Asset, Long> {
    Asset findAssetByName(String name);
}
