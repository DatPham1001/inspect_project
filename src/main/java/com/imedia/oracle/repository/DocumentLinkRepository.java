package com.imedia.oracle.repository;

import com.imedia.oracle.entity.DocumentLink;
import com.imedia.service.order.dto.ImageOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentLinkRepository extends JpaRepository<DocumentLink, Long> {
    DocumentLink findDocumentLinkById(Long id);

    @Query(value = "SELECT dl.ID,dl.PATH_TOPIC as path,ds.ID as imgId,ds.FILE_NAME as fileName \n" +
            "FROM DOCUMENT_LINKS dl\n" +
            "LEFT JOIN DOCUMENT_STORAGES ds ON ds.DOCUMENT_LINK_ID = dl.ID\n" +
            "WHERE ds.ID = :storageId", nativeQuery = true)
    ImageOM getImgStoragePath(@Param("storageId") Long documentStorageId);
}
