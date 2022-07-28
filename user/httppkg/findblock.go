package httppkg

import (
	"cabb/user/blockpkg"
	"cabb/user/txpkg"
	"encoding/json"
	"fmt"
	"net/http"
)

// Request 구조체
type BlockSearchRequest struct {
	TxId string `json:"txID"`
}

type JsonBlockResponse struct {
	Hash      [32]byte `json:"blockID"`
	Data      []byte   `json:"Data"`
	Timestamp []byte   `json:"Timestamp"`
}

//Json 타입으로 리턴해주기 위한 구조체

// func main() {
// 	request := &Request{}
// 	router := mux.NewRouter()
// 	router.HandleFunc("/searchBlock", request.ApplyCareer).Methods("Post")

// 	log.Fatal(http.ListenAndServe(":8080", router))
// }

func SearchBlock(w http.ResponseWriter, req *http.Request) {
	var body BlockSearchRequest

	bs := &blockpkg.Blocks{}

	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)
	if err != nil {
		fmt.Print(err)
		return
	}

	tmp := []byte(body.TxId)
	var txID [32]byte
	copy(txID[:], tmp)

	txs, bs := GChain()
	f := txpkg.FindBlockByTx(txID, bs)
	fmt.Println(txs)
	if f != nil {
		var response = JsonBlockResponse{Hash: f.Hash, Data: f.Data, Timestamp: f.Timestamp}
		json.NewEncoder(w).Encode(response)
	} else {
		fmt.Println("Txid 가 없습니다.")
	}

}
