package httppkg

import (
	"cabb/user/txpkg"
	"encoding/json"
	"net/http"
	_ "net/http"
)

// Request 구조체
type Request struct {
	Address string
	T       *txpkg.Tx
}

//Json 타입으로 리턴해주기 위한 구조체
type JsonResponse struct {
	Address string   `json:"address"`
	Txid    [32]byte `json:"txid"`
}

// Generate Transaction
func (r *Request) ApplyCareer(w http.ResponseWriter, req *http.Request) {
	Txs := txpkg.CreateTxDB() // [임시]비어있는 TXS
	Txid := Txs.AddTx(r.T)    // [임시]그 비어있는 TXS안에 Transaction을 넣고 Txid를 반환
	var response = JsonResponse{Address: r.Address, Txid: Txid}
	json.NewEncoder(w).Encode(response)
}
