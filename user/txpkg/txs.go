package txpkg

import "crypto/ecdsa"

type Txs struct {
	TxMap map[[32]byte]*Tx
}

// Txs(트랜잭션 DB 대용) 생성(최초 한번만 실행)
func CreateTxDB() *Txs {
	txs := &Txs{}
	txs.TxMap = make(map[[32]byte]*Tx)
	return txs
}

// Txs에 TX 저장
func (txs *Txs) AddTx(tx *Tx) {
	txs.TxMap[tx.TxID] = tx
}

//블록 생성 전 서명 확인
func (txs *Txs) CheckSign(txid [32]byte, pubKey *ecdsa.PublicKey) bool {
	check := false
	for i, j := range txs.TxMap {
		if i == txid {
			check = ecdsa.VerifyASN1(pubKey, txid[:], j.Sign)
			break
		}
	}
	return check
}
