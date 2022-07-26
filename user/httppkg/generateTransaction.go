package httppkg

import (
	"cabb/user/txpkg"
	"encoding/json"
	"fmt"
	"net/http"
	_ "net/http"
)

// Request 구조체
type Request struct {
	Address string    `json:"address"`
	T       *txpkg.Tx `json:"transaction"`
}

//Json 타입으로 리턴해주기 위한 구조체
type JsonResponse struct {
	Address string   `json:"address"`
	Txid    [32]byte `json:"txid"`
}

// Generate Transaction
func ApplyCareer(w http.ResponseWriter, req *http.Request) {
	var body Request

	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)
	//에러 체크
	if err != nil {
		fmt.Print(err)
		return
	}

	Txs := txpkg.CreateTxDB() // [임시] 최초에 만들어서 운용중인 Txs(DB) 가져와야함
	Txid := Txs.AddTx(body.T) // Txs(임시)에 트랜잭션 등록

	var response = JsonResponse{Address: body.Address, Txid: Txid}
	json.NewEncoder(w).Encode(response)
}
