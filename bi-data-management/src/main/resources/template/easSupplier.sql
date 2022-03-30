SELECT
    2 as cooperationType,
	FNUMBER AS easCode,
	FNAME_L2 AS companyName,
	FCREATETIME AS createTime,
	FTaxRegisterNo AS companyTaxkey,
	FMnemonicCode AS cpCode,
	FAddress AS companyAddress,
	FIndustryID AS parentIndustry,
	FSimpleName AS cpName,
	FIsInternalCompany AS isSelf,
	FArtificialPerson AS companyLegal,
	FUsedStatus AS isApproved,
	(SELECT wm_concat(FNUMBER) FROM EAS.T_SM_PurContract WHERE FBaseStatus = '4' AND T_BD_Supplier.fid = T_SM_PurContract.FSupplierID) AS contract_id_set FROM EAS.T_BD_Supplier where FNAME_L2 NOT IN ('默认','111','123','测试')

