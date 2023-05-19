/*
package org.first.finance.db.mongo.services;

import org.first.finance.configuration.IgnoreScan;
import org.first.finance.db.mongo.entity.Asset;
import org.first.finance.db.mongo.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@IgnoreScan
@Service
public class AssetOrmService {
    private AssetRepository assetRepository;

    public Asset findById(String id) {
        return assetRepository.findById(id).orElseThrow();
    }

    public Asset findAssetByName(String name) {
        return assetRepository.findAssetByName(name);
    }

    public Asset save(Asset asset) {
        return assetRepository.save(asset);
    }

    @Autowired
    public void setAssetRepository(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
}
*/
