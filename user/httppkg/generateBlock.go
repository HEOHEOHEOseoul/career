package httppkg

import (
	"cabb/user/blockpkg"
	"encoding/json"
	"fmt"
	"net/http"
)

type blkID struct {
	BlockID [32]byte `json:"BlockID"`
}

type reqBody struct {
	TxID string `json:"txID"`
	Data string `json:"data"`
}

func CreateNewBlock(w http.ResponseWriter, req *http.Request) {
	//var unmarshalErr *json.UnmarshalTypeError
	var body reqBody
	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)

	if err != nil {
		fmt.Print(err)
		return
	}
	prevHash := [32]byte{} // 가장 최근의 블록 해시를 불러와야 함
	height := 0            // 가장 최근 블록의 height 또는 블록체인의 길이를 저장

	tmp := []byte(body.TxID)
	var txID [32]byte
	copy(txID[:], tmp)

	data := body.Data
	res := &blkID{}
	res.BlockID = blockpkg.NewBlock(prevHash, height, txID, data).Hash
	json.NewEncoder(w).Encode(res)
}
