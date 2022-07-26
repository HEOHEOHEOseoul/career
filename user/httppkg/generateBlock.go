package httppkg

import (
	"cabb/user/blockpkg"
	"encoding/json"
	"fmt"
	"net/http"
)

// Response 데이터를 담을 구조체
type blkID struct {
	BlockID [32]byte `json:"BlockID"`
}

// Request 데이터가 담길 구조체
type reqBody struct {
	TxID string `json:"txID"`
	Data string `json:"data"`
}

func CreateNewBlock(w http.ResponseWriter, req *http.Request) {

	//request용 구조체 생성
	var body reqBody

	//Json 데이터 파싱
	decoder := json.NewDecoder(req.Body)
	decoder.DisallowUnknownFields()
	err := decoder.Decode(&body)
	//에러 체크
	if err != nil {
		fmt.Print(err)
		return
	}

	prevHash := [32]byte{} // (임시)가장 최근의 블록 해시를 불러와야 함
	height := 0            // (임시)가장 최근 블록의 height 또는 블록체인의 길이를 저장

	// string으로 받은 TxID를 [32]byte로 변환
	tmp := []byte(body.TxID)
	var txID [32]byte
	copy(txID[:], tmp)

	data := body.Data

	// response용 구조체 생성
	res := &blkID{}
	// 블록 패키지에 구현해놓은 NewBlock() 실행후 해시값 저장
	res.BlockID = blockpkg.NewBlock(prevHash, height, txID, data).Hash
	// response 구조체 JSON으로 인코딩후 전송
	json.NewEncoder(w).Encode(res)
}
